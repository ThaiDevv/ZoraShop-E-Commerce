package com.example.zorashopminishopee.module.product.service;

import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.product.dto.request.FilterSortRequest;
import com.example.zorashopminishopee.module.product.dto.response.InventoryLogResponse;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryLogService {
    Page<InventoryLogResponse> getInventoryLogs(String email, Long variantId, int page, int size);
    void createInventoryLogInCartOrder(List<CartItem> cartItem, Order order);
    void cancelReversedLog(OrderItem orderItems,  Order order);
    void deliverOrderLog(OrderItem orderItem, Order order);
}
