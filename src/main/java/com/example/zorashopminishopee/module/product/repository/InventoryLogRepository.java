package com.example.zorashopminishopee.module.product.repository;

import com.example.zorashopminishopee.module.product.entity.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    Page<InventoryLog> findByInventory_Variant_Id(Long inventoryVariantId, Pageable pageable);
}
