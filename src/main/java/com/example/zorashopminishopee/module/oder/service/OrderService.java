package com.example.zorashopminishopee.module.oder.service;

import com.example.zorashopminishopee.module.oder.dto.request.CheckoutCartRequest;
import com.example.zorashopminishopee.module.oder.dto.response.CheckoutResponse;

public interface OrderService {
    public CheckoutResponse orderFromCart(String email, CheckoutCartRequest checkoutCartRequest);
}
