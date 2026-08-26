package com.example.zorashopminishopee.module.product.service.impl;

import com.example.zorashopminishopee.module.product.emun.InventoryLogType;
import com.example.zorashopminishopee.module.product.entity.Inventory;
import com.example.zorashopminishopee.module.product.entity.InventoryLog;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.InventoryLogRepository;
import com.example.zorashopminishopee.module.product.repository.InventoryRepository;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
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
}
