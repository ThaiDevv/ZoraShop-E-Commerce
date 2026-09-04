package com.example.zorashopminishopee.module.product.service.impl;

import com.example.zorashopminishopee.common.exception.ForbiddenException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.product.dto.response.InventoryLogResponse;
import com.example.zorashopminishopee.module.product.entity.Inventory;
import com.example.zorashopminishopee.module.product.entity.InventoryLog;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.enums.InventoryLogType;
import com.example.zorashopminishopee.module.product.repository.InventoryLogRepository;
import com.example.zorashopminishopee.module.product.repository.ProductRepository;
import com.example.zorashopminishopee.module.product.repository.ProductVariantRepository;
import com.example.zorashopminishopee.module.product.service.InventoryLogService;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.repository.ShopRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class InventoryLogServiceImpl implements InventoryLogService {
    private final InventoryLogRepository inventoryLogRepository;
    private final ShopRepository shopRepository;
    private final ProductVariantRepository productVariantRepository;
    @PersistenceContext
    private EntityManager em;
    @Override
    public Page<InventoryLogResponse> getInventoryLogs(String email, Long variantId, int page, int size) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
        Shops shop = shopRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (!productVariant.getProduct().getShop().getId().equals(shop.getId())) {
            throw new ForbiddenException("Bạn không có quyền xem inventoryLog của Shop khác!");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InventoryLog> inventoryLogPage = inventoryLogRepository.findByInventory_Variant_Id(variantId, pageable);
        return inventoryLogPage.map(
                InventoryLog -> {
                    return new InventoryLogResponse(
                            InventoryLog.getId(),
                            InventoryLog.getInventory().getId(),
                            InventoryLog.getType(),
                            InventoryLog.getQuantityChange(),
                            InventoryLog.getQuantityAfter(),
                            InventoryLog.getReason(),
                            InventoryLog.getReferenceId(),
                            InventoryLog.getCreatedAt()
                    );
                }
        );
    }

    @Override
    public void createInventoryLogInCartOrder(List<CartItem> cartItems, Order order) {
        cartItems.forEach(cartItem -> {
            Inventory inventory = cartItem.getVariant().getInventory();
            inventory.setReserved(inventory.getReserved() + cartItem.getQuantity());
            em.flush();
            em.refresh(inventory);
            cartItem.getVariant().setStock(inventory.getAvailable());
            String reason = "Tạm giữ " + cartItem.getQuantity() + " sản phẩm cho đơn hàng #" + order.getOrderNumber();
            InventoryLog log = InventoryLog.builder()
                    .inventory(inventory)
                    .type(InventoryLogType.RESERVED)
                    .quantityChange(cartItem.getQuantity())
                    .quantityAfter(inventory.getAvailable())
                    .reason(reason)
                    .referenceId(order.getId())
                    .createdAt(LocalDateTime.now())
                    .build();
            inventoryLogRepository.save(log);
        });
    }

    @Override
    public void cancelReversedLog(OrderItem orderItems, Order order) {
        String reason = "Bỏ tạm giữ" + orderItems.getQuantity() + " sản phẩm cho đơn hàng #"  + order.getOrderNumber();
        InventoryLog log = InventoryLog.builder()
                .inventory(orderItems.getVariant().getInventory())
                .type(InventoryLogType.RELEASED)
                .quantityChange(orderItems.getQuantity())
                .quantityAfter(orderItems.getVariant().getStock())
                .reason(reason)
                .referenceId(order.getId())
                .createdAt(LocalDateTime.now())
                .build();
        inventoryLogRepository.save(log);
    }

    @Override
    public void deliverOrderLog(OrderItem orderItems, Order order) {
        String reason = "Bỏ tạm giữ và xuất " + orderItems.getQuantity() + " sản phẩm cho đơn hàng #"  + order.getOrderNumber();
        InventoryLog log = InventoryLog.builder()
                .inventory(orderItems.getVariant().getInventory())
                .type(InventoryLogType.OUT)
                .quantityChange(orderItems.getQuantity())
                .quantityAfter(orderItems.getVariant().getStock())
                .reason(reason)
                .referenceId(order.getId())
                .createdAt(LocalDateTime.now())
                .build();
        inventoryLogRepository.save(log);
    }
}
