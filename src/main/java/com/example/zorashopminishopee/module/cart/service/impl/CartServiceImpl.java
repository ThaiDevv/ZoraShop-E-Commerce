package com.example.zorashopminishopee.module.cart.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.InsufficientStockException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.cart.dto.request.CreateCartItemRequest;
import com.example.zorashopminishopee.module.cart.dto.response.CartItemResponse;
import com.example.zorashopminishopee.module.cart.dto.response.CartResponse;
import com.example.zorashopminishopee.module.cart.dto.response.CartShopGroupResponse;
import com.example.zorashopminishopee.module.cart.entity.Cart;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.cart.repository.CartItemRepository;
import com.example.zorashopminishopee.module.cart.repository.CartRepository;
import com.example.zorashopminishopee.module.cart.service.CartService;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.ProductVariantRepository;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
                variant.getId(),
                variant.getSku(),
                variant.getProduct().getName(),
                variant.getVariantName(),
                variant.getPrice(),
                variant.getProduct().getOriginalPrice(),
                cartItem.getQuantity(),
                variant.getStock(),
                variant.getImageUrl()
        );
    }
    public int checkStock(Optional<CartItem> existingItemOpt, ProductVariant variant, int quantity) {
        int currentQtyInCart = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int newTotalQuantity = currentQtyInCart + quantity;

        if (variant.getStock() < newTotalQuantity) {
            throw new InsufficientStockException(
                    String.format("Tồn kho không đủ! Trong kho còn %d, giỏ hàng đã có %d, bạn muốn thêm %d",
                            variant.getStock(), currentQtyInCart, quantity)
            );
        }
        return newTotalQuantity;
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
        int newTotalQuantity = checkStock(existingItemOpt, variant, request.quantity());

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

    @Override
    @Transactional
    public void removeFromCart(String email, Long id) {
        Cart cart = getCartByEmail(email);
        cartItemRepository.delete(cartItemRepository.findById_AndCart_Id(id, cart.getId()).orElseThrow(
                () -> new ResourceNotFoundException("CartItem not found with ID: " + id)
        ));
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItem(String email, Long id, Integer quantity) {
        if(quantity == null) {
            throw new BadRequestException("Quantity cannot be null");
        }
        Cart cart = getCartByEmail(email);
        Optional<CartItem> cartItem = cartItemRepository.findById_AndCart_Id(id, cart.getId());
        if (quantity <= 0){
            removeFromCart(email, id);
            return null;
        }
        ProductVariant variant;
        if (cartItem.isPresent()) {
            variant = cartItem.get().getVariant();
        }else {
            throw new ResourceNotFoundException("CartItem not found with ID: " + id);
        }
        int newQuantity = checkStock(cartItem, variant, quantity - cartItem.get().getQuantity());
        cartItem.get().setQuantity(newQuantity);
        cartItemRepository.save(cartItem.get());
        return mapToCartItemResponse(cartItem.get());
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        Cart cart = getCartByEmail(email);
        List<CartItem> cartItems = cartItemRepository.findByCartIdWithProductAndShop(cart.getId());
        Map<Shops, List<CartItem>> itemsByShopMap = cartItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getVariant().getProduct().getShop(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        List<CartShopGroupResponse> cartShops = itemsByShopMap.entrySet()
                .stream().map(entry
                        -> {
                            Shops shop = entry.getKey();
                            List<CartItemResponse>   cartItemList = entry.getValue().stream().map(
                                    this::mapToCartItemResponse
                            ).toList();
                            return new CartShopGroupResponse(
                                    shop.getId(),
                                    shop.getName(),
                                    shop.getLogoUrl(),
                                    cartItemList
                            );
                        }
                ).toList();
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getVariant().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalItem = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        return new CartResponse(
                cart.getId(),
                totalAmount,
                totalItem,
                cartShops
        );
    }

}
