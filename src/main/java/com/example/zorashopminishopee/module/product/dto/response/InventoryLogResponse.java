package com.example.zorashopminishopee.module.product.dto.response;

import com.example.zorashopminishopee.module.product.emun.InventoryLogType;
import com.example.zorashopminishopee.module.product.entity.Inventory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public record InventoryLogResponse(
        Long id,
        Long inventoryId,
        InventoryLogType type,
        Integer quantityChange,
        Integer quantityAfter,
        String reason,
        Long referenceId,
        LocalDateTime createdAt
) {
}
