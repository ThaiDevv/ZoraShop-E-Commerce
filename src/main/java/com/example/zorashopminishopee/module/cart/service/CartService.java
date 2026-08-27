package com.example.zorashopminishopee.module.cart.service;

import com.example.zorashopminishopee.module.cart.dto.request.CreateCartItemRequest;
import com.example.zorashopminishopee.module.cart.dto.response.CartItemResponse;
import com.example.zorashopminishopee.module.cart.entity.Cart;

public interface CartService {
    public CartItemResponse addToCart(String email, CreateCartItemRequest createCartItemRequest);

}
