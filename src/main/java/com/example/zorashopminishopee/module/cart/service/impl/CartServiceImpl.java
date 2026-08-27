package com.example.zorashopminishopee.module.cart.service.impl;

import com.example.zorashopminishopee.common.exception.InsufficientStockException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.cart.dto.request.CreateCartItemRequest;
import com.example.zorashopminishopee.module.cart.dto.response.CartItemResponse;
import com.example.zorashopminishopee.module.cart.entity.Cart;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.cart.repository.CartItemRepository;
import com.example.zorashopminishopee.module.cart.repository.CartRepository;
import com.example.zorashopminishopee.module.cart.service.CartService;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.ProductVariantRepository;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    private Cart getCartByEmail(String email) {
        Cart cart = cartRepository.findByUser_Email(email);
        if (cart == null) {
            log.info("User {} chưa có giỏ hàng, tiến hành tạo giỏ hàng mới", email);
            Users user = Optional.ofNullable(userRepository.findByEmail(email))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        }
        return cart;
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        ProductVariant variant = cartItem.getVariant();
        return new CartItemResponse(
                cartItem.getId(),
                variant.getProduct().getName(),
                variant.getVariantName(),
                cartItem.getQuantity(),
                variant.getImageUrl()
        );
    }

    @Override
    @Transactional
    public CartItemResponse addToCart(String email, CreateCartItemRequest request) {
        Cart cart = getCartByEmail(email);

        ProductVariant variant = productVariantRepository.findBySku(request.sku())
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with SKU: " + request.sku()));

        Optional<CartItem> existingItemOpt = cartItemRepository
                .findByCart_IdAndVariant_Id(cart.getId(), variant.getId());

        CartItem cartItem;
        int currentQtyInCart = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int newTotalQuantity = currentQtyInCart + request.quantity();

        if (variant.getStock() < newTotalQuantity) {
            throw new InsufficientStockException(
                    String.format("Tồn kho không đủ! Trong kho còn %d, giỏ hàng đã có %d, bạn muốn thêm %d",
                            variant.getStock(), currentQtyInCart, request.quantity())
            );
        }

        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(newTotalQuantity);
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.quantity())
                    .build();
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        return mapToCartItemResponse(cartItem);
    }
}
