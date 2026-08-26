package com.example.zorashopminishopee.module.product.repository;

import com.example.zorashopminishopee.module.product.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
}
