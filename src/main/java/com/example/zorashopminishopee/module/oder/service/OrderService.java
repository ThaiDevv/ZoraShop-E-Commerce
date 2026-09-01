package com.example.zorashopminishopee.module.oder.service;

import com.example.zorashopminishopee.module.oder.dto.request.CheckoutCartRequest;
import com.example.zorashopminishopee.module.oder.dto.response.CancelOrderResponse;
import com.example.zorashopminishopee.module.oder.dto.response.CheckoutResponse;
import com.example.zorashopminishopee.module.oder.dto.response.HistoryOrderResponse;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    public CheckoutResponse orderFromCart(String email, CheckoutCartRequest checkoutCartRequest);
    public Page<HistoryOrderResponse> getHistoryOrders(String email, StatusType type, int page, int size);
    public CancelOrderResponse cancelOrder(String email, Long orderId, String reason);
}
