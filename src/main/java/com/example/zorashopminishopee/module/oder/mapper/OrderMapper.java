package com.example.zorashopminishopee.module.oder.mapper;

import com.example.zorashopminishopee.module.oder.dto.response.*;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public HistoryOrderItemResponse mapToHistoryOrderItemResponse(OrderItem orderItem) {
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

    public HistoryOrderResponse mapToHistoryOrderResponse(Order order) {
        if (order == null) return null;
        return new HistoryOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getShop() != null ? order.getShop().getId() : null,
                order.getShopImageUrl(),
                order.getShop() != null ? order.getShop().getName() : null,
                order.getTotalAmount(),
                order.getSubtotal(),
                order.getStatus(),
                order.getCreatedDate(),
                order.getOrderItems() != null
                        ? order.getOrderItems().stream().map(this::mapToHistoryOrderItemResponse).toList()
                        : List.of()
        );
    }

    public CancelOrderResponse mapToCancelOrderResponse(Order order, String reason) {
        if (order == null) return null;
        return new CancelOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                reason,
                LocalDateTime.now()
        );
    }

    public DetailOrderResponse mapToDetailOrderResponse(Order order) {
        if (order == null) return null;
        String receiveAddress = order.getAddress() != null
                ? String.format("%s, %s, %s, %s",
                order.getAddress().getStreet(),
                order.getAddress().getWard(),
                order.getAddress().getDistrict(),
                order.getAddress().getCity())
                : null;

        Long shopId = order.getShop() != null ? order.getShop().getId() : null;
        String shopName = order.getShop() != null ? order.getShop().getName() : null;

        Long paymentId = order.getPayment() != null ? order.getPayment().getId() : null;
        PaymentMethod paymentMethod = order.getPayment() != null ? order.getPayment().getMethod() : null;
        String transactionId = order.getPayment() != null ? order.getPayment().getTransactionId() : null;

        String nameReceive = order.getAddress() != null ? order.getAddress().getFullName() : null;
        String phoneReceive = order.getAddress() != null ? order.getAddress().getPhone() : null;

        List<OrderItemResponse> itemResponses = order.getOrderItems() != null
                ? order.getOrderItems().stream().map(this::toOrderItemResponse).toList()
                : List.of();

        return new DetailOrderResponse(
                order.getId(),
                shopId,
                paymentId,
                order.getOrderNumber(),
                paymentMethod,
                transactionId,
                shopName,
                order.getShopImageUrl(),
                nameReceive,
                phoneReceive,
                receiveAddress,
                order.getSubtotal(),
                order.getShippingFee(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedDate(),
                itemResponses
        );
    }

    public Page<OrderSummaryResponse> mapToOrderSummaryResponse(Page<Order> orderPage) {
        if (orderPage == null) return Page.empty();
        return orderPage.map(order -> new OrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getAddress() != null ? order.getAddress().getFullName() : null,
                order.getTotalAmount(),
                order.getStatus(),
                order.getPayment() != null ? order.getPayment().getMethod() : null,
                order.getPayment() != null ? order.getPayment().getStatus() : null,
                order.getOrderItems() != null ? order.getOrderItems().size() : 0,
                order.getCreatedDate()
        ));
    }

    public SellerOrderDetailResponse mapToSellerOrderDetailResponse(Order order) {
        if (order == null) return null;

        String receiveAddress = order.getAddress() != null
                ? String.format("%s, %s, %s, %s",
                order.getAddress().getStreet(),
                order.getAddress().getWard(),
                order.getAddress().getDistrict(),
                order.getAddress().getCity())
                : null;

        String receiverName = order.getAddress() != null ? order.getAddress().getFullName() : null;
        String receiverPhone = order.getAddress() != null ? order.getAddress().getPhone() : null;

        PaymentMethod paymentMethod = order.getPayment() != null ? order.getPayment().getMethod() : null;
        PaymentStatus paymentStatus = order.getPayment() != null ? order.getPayment().getStatus() : null;
        String transactionId = order.getPayment() != null ? order.getPayment().getTransactionId() : null;
        LocalDateTime paidAt = order.getPayment() != null ? order.getPayment().getPaidAt() : null;

        List<OrderItemResponse> itemResponses = order.getOrderItems() != null
                ? order.getOrderItems().stream().map(this::toOrderItemResponse).toList()
                : List.of();

        return new SellerOrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getNote(),
                receiverName,
                receiverPhone,
                receiveAddress,
                order.getSubtotal(),
                order.getShippingFee(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                paymentMethod,
                paymentStatus,
                transactionId,
                paidAt,
                itemResponses,
                order.getCreatedDate()
        );
    }
}
