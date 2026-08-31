package com.example.zorashopminishopee.module.cart.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.cart.dto.request.CreateCartItemRequest;
import com.example.zorashopminishopee.module.cart.dto.response.CartItemResponse;
import com.example.zorashopminishopee.module.cart.dto.response.CartResponse;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        CartResponse response = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PostMapping("/item")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(Authentication authentication,@Valid @RequestBody CreateCartItemRequest createCartItemRequest) {
        CartItemResponse response = cartService.addToCart(authentication.getName(), createCartItemRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PutMapping("/item/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItem (@PathVariable Long id, Authentication authentication, @RequestParam  Integer quantity) {
        CartItemResponse cartItemResponse = cartService.updateCartItem(authentication.getName(), id, quantity);
        return ResponseEntity.ok(ApiResponse.success(cartItemResponse));
    }
    @DeleteMapping("/item/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem (@PathVariable Long id, Authentication authentication) {
        cartService.removeFromCart(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteCart (Authentication authentication) {
        cartService.deleteCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
