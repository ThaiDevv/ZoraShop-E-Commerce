package com.example.zorashopminishopee.module.oder.service;

import com.example.zorashopminishopee.module.oder.dto.request.CheckoutCartRequest;
import com.example.zorashopminishopee.module.oder.dto.response.*;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import org.springframework.data.domain.Page;

public interface OrderService {
    CheckoutResponse orderFromCart(String email, CheckoutCartRequest checkoutCartRequest);
    Page<HistoryOrderResponse> getHistoryOrders(String email, StatusType type, int page, int size);
    CancelOrderResponse cancelOrder(String email, Long orderId, String reason);
    DetailOrderResponse detailOrder(String email, Long orderId);
    Page<OrderSummaryResponse> getOrderSummary(String email, StatusType type, int page, int size);
    SellerOrderDetailResponse getSellerOrderDetail(String email, Long orderId);
}
