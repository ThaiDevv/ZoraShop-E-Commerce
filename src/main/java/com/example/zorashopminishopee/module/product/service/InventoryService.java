package com.example.zorashopminishopee.module.product.service;

import com.example.zorashopminishopee.module.product.entity.Inventory;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;

public interface InventoryService {
    Inventory createInventory(ProductVariant productVariant, int quantity);
    void updateStock(ProductVariant productVariant, int quantity);
}
