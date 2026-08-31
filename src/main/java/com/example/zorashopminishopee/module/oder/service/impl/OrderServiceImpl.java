package com.example.zorashopminishopee.module.oder.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.cart.repository.CartItemRepository;
import com.example.zorashopminishopee.module.cart.service.CartService;
import com.example.zorashopminishopee.module.oder.dto.request.CheckoutCartRequest;
import com.example.zorashopminishopee.module.oder.dto.response.CheckoutResponse;
import com.example.zorashopminishopee.module.oder.dto.response.OrderItemResponse;
import com.example.zorashopminishopee.module.oder.dto.response.OrderResponse;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.mapper.OrderMapper;
import com.example.zorashopminishopee.module.oder.repository.OrderItemRepository;
import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.oder.service.OrderService;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.service.InventoryLogService;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import com.example.zorashopminishopee.module.users.entity.Address;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.repository.AddressRepository;
import com.example.zorashopminishopee.module.users.repository.UserRepository;
import com.example.zorashopminishopee.module.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryService inventoryService;
    private final InventoryLogService inventoryLogService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final OrderMapper orderMapper;
    public String generateOrderNumber() {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }
    @Override
    @Transactional
    public CheckoutResponse orderFromCart(String email, CheckoutCartRequest request) {
        List<Order> orders = new ArrayList<>();
        List<CartItem> cartItems = cartItemRepository.findByIdAndCart_User_Email(request.cartItemIds(),  email);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Danh sách sản phẩm giỏ hàng không tồn tại hoặc không hợp lệ!");
        }
        inventoryService.checkStockFromCartItem(cartItems);
        Users user = cartItems.get(0).getCart().getUser();
        Address address  = addressRepository.findById(request.addressId()).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy địa chỉ")
        );
        Map<Shops, List<CartItem>> shopsListMap = cartItems.stream().collect(Collectors.groupingBy(
                item -> item.getVariant().getProduct().getShop(),
                LinkedHashMap::new,
                Collectors.toList()
        ));
        shopsListMap.forEach((key, value) -> {
            String orderNumber = generateOrderNumber();
            BigDecimal subtotal = value.stream().map(
                    CartItem -> CartItem
                            .getVariant()
                            .getPrice()
                            .multiply(BigDecimal.valueOf(CartItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal shippingFee = BigDecimal.ZERO;
            BigDecimal discountAmount = BigDecimal.ZERO;
            Order order = Order.builder()
                    .orderNumber(orderNumber)
                    .user(user)
                    .shop(key)
                    .address(address)
                    .voucherId(null)
                    .subtotal(subtotal)
                    .shippingFee(shippingFee)
                    .discountAmount(discountAmount)
                    .totalAmount(subtotal.add(shippingFee).subtract(discountAmount))
                    .status(StatusType.PENDING)
                    .note(request.note())
                    .build();
            orderRepository.save(order);
            inventoryLogService.createInventoryLogInCartOrder(value, order);
            List<OrderItem> orderItems = value.stream().map(
                    CartItem -> {
                        ProductVariant variant = CartItem.getVariant();
                         OrderItem item = OrderItem.builder()
                                .order(order)
                                .variant(variant)
                                .productName(variant.getProduct().getName())
                                .variantName(variant.getVariantName())
                                .price(variant.getPrice())
                                .quantity(CartItem.getQuantity())
                                .subtotal(variant.getPrice().multiply(BigDecimal.valueOf(CartItem.getQuantity())))
                                .build();
                        return orderItemRepository.save(item);
                    }
            ).toList();
            order.setOrderItems(orderItems);
            orderRepository.save(order);
            orders.add(order);
        });
        return orderMapper.toCheckoutResponse(orders);
    }
}
