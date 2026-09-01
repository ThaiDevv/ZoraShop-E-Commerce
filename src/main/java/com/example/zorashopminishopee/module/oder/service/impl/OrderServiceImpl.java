package com.example.zorashopminishopee.module.oder.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.cart.repository.CartItemRepository;
import com.example.zorashopminishopee.module.oder.dto.request.CheckoutCartRequest;
import com.example.zorashopminishopee.module.oder.dto.response.CheckoutResponse;
import com.example.zorashopminishopee.module.oder.dto.response.HistoryOrderResponse;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.mapper.OrderMapper;
import com.example.zorashopminishopee.module.oder.repository.OrderItemRepository;
import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.oder.service.OrderService;
import com.example.zorashopminishopee.module.oder.specification.OrderSpecification;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.service.InventoryLogService;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import com.example.zorashopminishopee.module.users.entity.Address;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.repository.AddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generateOrderNumber() {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }

    @Override
    @Transactional
    public CheckoutResponse orderFromCart(String email, CheckoutCartRequest request) {
        List<Order> orders = new ArrayList<>();
        List<CartItem> cartItems = cartItemRepository.findByIdAndCart_User_Email(request.cartItemIds(), email);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Danh sách sản phẩm giỏ hàng không tồn tại hoặc không hợp lệ!");
        }
        inventoryService.checkStockFromCartItem(cartItems);
        Users user = cartItems.get(0).getCart().getUser();
        Address address = addressRepository.findByIdAndUser_Email(request.addressId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ nhận hàng không hợp lệ hoặc không thuộc về bạn!"));

        Map<Shops, List<CartItem>> shopsListMap = cartItems.stream().collect(Collectors.groupingBy(
                item -> item.getVariant().getProduct().getShop(),
                LinkedHashMap::new,
                Collectors.toList()
        ));
        shopsListMap.forEach((shop, itemsOfShop) -> {
            String orderNumber = generateOrderNumber();
            BigDecimal subtotal = itemsOfShop.stream().map(
                    cartItem -> cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            ).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shippingFee = BigDecimal.ZERO;
            BigDecimal discountAmount = BigDecimal.ZERO;

            Order order = Order.builder()
                    .orderNumber(orderNumber)
                    .user(user)
                    .shop(shop)
                    .address(address)
                    .shopImageUrl(shop.getLogoUrl())
                    .voucherId(null)
                    .subtotal(subtotal)
                    .shippingFee(shippingFee)
                    .discountAmount(discountAmount)
                    .totalAmount(subtotal.add(shippingFee).subtract(discountAmount))
                    .status(StatusType.PENDING)
                    .note(request.note())
                    .build();

            orderRepository.save(order);
            inventoryLogService.createInventoryLogInCartOrder(itemsOfShop, order);

            List<OrderItem> orderItems = itemsOfShop.stream().map(
                    cartItem -> {
                        ProductVariant variant = cartItem.getVariant();
                        OrderItem item = OrderItem.builder()
                                .order(order)
                                .variant(variant)
                                .productId(variant.getProduct().getId())
                                .productImg(variant.getImageUrl())
                                .productName(variant.getProduct().getName())
                                .variantName(variant.getVariantName())
                                .price(variant.getPrice())
                                .quantity(cartItem.getQuantity())
                                .subtotal(variant.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                                .build();
                        return orderItemRepository.save(item);
                    }
            ).toList();

            order.setOrderItems(orderItems);
            orderRepository.save(order);
            orders.add(order);
        });

        cartItemRepository.deleteAll(cartItems);
        return orderMapper.toCheckoutResponse(orders);
    }

    @Override
    public Page<HistoryOrderResponse> getHistoryOrders(String email,StatusType status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Specification<Order> spec = Specification.where(OrderSpecification.hasUserEmail(email))
                .and(OrderSpecification.hasStatus(status));
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(orderMapper::mapToHistoryOrderResponse);
    }

}
