package com.example.zorashopminishopee.module.oder.mapper;

import com.example.zorashopminishopee.module.oder.dto.response.*;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {
    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) return null;
        return new OrderItemResponse(
                item.getId(),
                item.getVariant() != null ? item.getVariant().getId() : null,
                item.getProductName(),
                item.getProductId(),
                item.getProductImg(),
                item.getVariantName(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
    public OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;
        List<OrderItemResponse> itemResponses = order.getOrderItems() != null
                ? order.getOrderItems().stream().map(this::toOrderItemResponse).toList()
                : List.of();

        String receiveAddress = order.getAddress() != null
                ? String.format("%s, %s, %s, %s",
                order.getAddress().getStreet(),
                order.getAddress().getWard(),
                order.getAddress().getDistrict(),
                order.getAddress().getCity())
                : null;

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getShop() != null ? order.getShop().getId() : null,
                order.getShop() != null ? order.getShop().getName() : null,
                order.getAddress() != null ? order.getAddress().getId() : null,
                order.getAddress() != null ? order.getAddress().getFullName() : null,
                order.getShopImageUrl(),
                order.getAddress() != null ? order.getAddress().getPhone() : null,
                receiveAddress,
                order.getSubtotal(),
                order.getShippingFee(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getNote(),
                order.getCreatedDate(),
                itemResponses
        );
    }
    public CheckoutResponse toCheckoutResponse(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return new CheckoutResponse(BigDecimal.ZERO, 0, List.of());
        }
        BigDecimal grandTotal = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::toOrderResponse)
                .toList();
        return new CheckoutResponse(grandTotal, orders.size(), orderResponses);
    }
    public HistoryOrderItemResponse mapToHistoryOrderItemResponse(OrderItem orderItem){
        if (orderItem == null) return null;
        return new HistoryOrderItemResponse(
                orderItem.getId(),
                orderItem.getProductId(), 
                orderItem.getProductName(),
                orderItem.getProductImg(),
                orderItem.getVariantName(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getSubtotal()
        );
    }
    public HistoryOrderResponse mapToHistoryOrderResponse(Order order){
        if (order == null) return null;
        return new HistoryOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getShop().getId(),
                order.getShopImageUrl(),
                order.getShop().getName(),
                order.getTotalAmount(),
                order.getSubtotal(),
                order.getStatus(),
                order.getCreatedDate(),
                order.getOrderItems().stream().map(this::mapToHistoryOrderItemResponse).toList()
        );
    }
}
