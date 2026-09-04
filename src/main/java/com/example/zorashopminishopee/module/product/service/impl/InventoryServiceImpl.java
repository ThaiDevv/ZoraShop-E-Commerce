package com.example.zorashopminishopee.module.product.service.impl;

import com.example.zorashopminishopee.common.exception.ForbiddenException;
import com.example.zorashopminishopee.common.exception.InsufficientStockException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.cart.repository.CartItemRepository;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.product.enums.InventoryLogType;
import com.example.zorashopminishopee.module.product.entity.Inventory;
import com.example.zorashopminishopee.module.product.entity.InventoryLog;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.InventoryLogRepository;
import com.example.zorashopminishopee.module.product.repository.InventoryRepository;
import com.example.zorashopminishopee.module.product.repository.ProductVariantRepository;
import com.example.zorashopminishopee.module.product.service.InventoryLogService;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.repository.ShopRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    @PersistenceContext
    private EntityManager em;
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ShopRepository shopRepository;
    private final InventoryLogService inventoryLogService;
    @Override
    @Transactional
    public Inventory createInventory(ProductVariant productVariant, int quantity) {
        Inventory inventory = Inventory.builder()
                .variant(productVariant)
                .quantity(quantity)
                .build();
        Inventory savedInventory = inventoryRepository.save(inventory);
        InventoryLog log = InventoryLog.builder()
                .inventory(savedInventory)
                .type(InventoryLogType.IN)
                .quantityChange(quantity)
                .quantityAfter(quantity)
                .reason("Khởi tạo kho cho sản phẩm mới")
                .createdAt(LocalDateTime.now())
                .build();
        inventoryLogRepository.save(log);
        return savedInventory;
    }

    @Override
    @Transactional
    public void updateStock(ProductVariant productVariant, int quantity) {
        Inventory inventory = productVariant.getInventory();
        if (inventory == null) {
            createInventory(productVariant, quantity);
            return;
        }

        int oldQuantity = inventory.getQuantity();
        int delta = quantity - oldQuantity;
        if (delta == 0) {
            return;
        }
        InventoryLogType type = delta > 0 ? InventoryLogType.IN : InventoryLogType.OUT;
        String reason = delta > 0
                ? "Thêm số lượng hàng tồn kho: " + delta
                : "Giảm số lượng hàng tồn kho: " + Math.abs(delta);
        inventory.setQuantity(quantity);
        InventoryLog log = InventoryLog.builder()
                .inventory(inventory)
                .quantityChange(Math.abs(delta))
                .quantityAfter(quantity)
                .type(type)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        inventoryLogRepository.save(log);
    }

    @Override
    @Transactional
    public void updateStockApi(String email, Long variantId, int quantity) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
        Shops shop = shopRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (!productVariant.getProduct().getShop().getId().equals(shop.getId())) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa sản phẩm của Shop khác!");
        }
        updateStock(productVariant, quantity);
    }

    @Override
    @Transactional
    public void checkStockFromCartItem(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            if(cartItem.getVariant().getInventory() == null) {
                cartItem.getVariant().setInventory(
                        createInventory(cartItem.getVariant(),cartItem.getVariant().getStock())
                );
            }
            int stock = cartItem.getVariant().getInventory().getAvailable();
            if (cartItem.getQuantity() > stock) {
                throw new InsufficientStockException(
                        "Số lượng trong kho không đủ với đơn hàng: " + cartItem.getVariant().getVariantName()
                );
            }
        }
    }

    @Override
    public void cancelReserved(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            Inventory inventory = orderItem.getVariant().getInventory();
            inventory.setReserved(inventory.getReserved() - orderItem.getQuantity());
            em.flush();
            em.refresh(inventory);
            orderItem.getVariant().setStock(inventory.getAvailable());
            inventoryLogService.cancelReversedLog(orderItem, orderItem.getOrder());
        });
    }

    @Override
    public void deliverOrder(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            Inventory inventory = orderItem.getVariant().getInventory();
            inventory.setReserved(inventory.getReserved() - orderItem.getQuantity());
            inventory.setQuantity(inventory.getQuantity() - orderItem.getQuantity());
            em.flush();
            em.refresh(inventory);
            orderItem.getVariant().setStock(inventory.getAvailable());
            inventoryLogService.deliverOrderLog(orderItem, orderItem.getOrder());
        });
    }
}
