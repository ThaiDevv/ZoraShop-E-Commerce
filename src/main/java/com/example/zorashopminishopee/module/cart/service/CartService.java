package com.example.zorashopminishopee.module.cart.service;

import com.example.zorashopminishopee.module.cart.dto.request.CreateCartItemRequest;
import com.example.zorashopminishopee.module.cart.dto.response.CartItemResponse;
import com.example.zorashopminishopee.module.cart.dto.response.CartResponse;
import com.example.zorashopminishopee.module.cart.entity.Cart;

public interface CartService {
    public CartItemResponse addToCart(String email, CreateCartItemRequest createCartItemRequest);
    public void removeFromCart(String email, Long id);
    public CartItemResponse updateCartItem(String email, Long id, Integer quantity);
    public CartResponse getCart(String email);
}
