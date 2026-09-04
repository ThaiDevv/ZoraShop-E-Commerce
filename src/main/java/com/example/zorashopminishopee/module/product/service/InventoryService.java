package com.example.zorashopminishopee.module.product.service;

import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.product.entity.Inventory;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import jakarta.transaction.Transactional;

import java.util.List;

public interface InventoryService {
    Inventory createInventory(ProductVariant productVariant, int quantity);
    void updateStock(ProductVariant productVariant, int quantity);
    void updateStockApi(String email, Long id, int quantity);
    void checkStockFromCartItem(List<CartItem> cartItems);
    void cancelReserved(List<OrderItem> orderItems);
    void deliverOrder(List<OrderItem> orderItems);
}
