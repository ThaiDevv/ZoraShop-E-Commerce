package com.example.zorashopminishopee.module.oder.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import com.example.zorashopminishopee.module.cart.repository.CartItemRepository;
import com.example.zorashopminishopee.module.oder.dto.request.CheckoutCartRequest;
import com.example.zorashopminishopee.module.oder.dto.response.*;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.mapper.OrderMapper;
import com.example.zorashopminishopee.module.oder.repository.OrderItemRepository;
import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.oder.service.OrderService;
import com.example.zorashopminishopee.module.oder.specification.OrderSpecification;
import com.example.zorashopminishopee.module.payment.entity.Payment;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import com.example.zorashopminishopee.module.payment.service.PaymentService;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.service.InventoryLogService;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import com.example.zorashopminishopee.module.users.entity.Address;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.repository.AddressRepository;
import com.example.zorashopminishopee.module.users.repository.ShopRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryService inventoryService;
    private final InventoryLogService inventoryLogService;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final PaymentService paymentService;

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
        String groupTxnId;
        if(request.paymentMethod() != PaymentMethod.COD) {
            groupTxnId = "TXN-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        } else {
            groupTxnId = "";
        }
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
            Payment payment= paymentService.createPayment(order, request.paymentMethod(), groupTxnId);
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
            order.setPayment(payment);
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

    @Override
    @Transactional
    public CancelOrderResponse cancelOrder(String email, Long orderId, String reason) {
        Order order = orderRepository.findForCancelByUserEmailAndId(email, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc bạn không có quyền truy cập!"));

        if (order.getStatus() != StatusType.PENDING && order.getStatus() != StatusType.CONFIRMED) {
            throw new BadRequestException("Không thể hủy đơn hàng đang trong quá trình vận chuyển hoặc đã hoàn thành!");
        }
        inventoryService.cancelReserved(order.getOrderItems());
        order.setStatus(StatusType.CANCELLED);
        orderRepository.save(order);
        return orderMapper.mapToCancelOrderResponse(order, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public DetailOrderResponse detailOrder(String email, Long orderId) {
        Order order = orderRepository.findDetailByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc bạn không có quyền truy cập!"));
        return orderMapper.mapToDetailOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrderSummary(String email, StatusType type, int page, int size) {
        Shops shop = shopRepository.findByUser_Email(email).orElseThrow(
                () -> new ResourceNotFoundException("Tài khoản chưa đăng ký mở Shop!"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Specification<Order> spec = Specification.where(OrderSpecification.hasShop(shop))
                .and(OrderSpecification.hasStatus(type));
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orderMapper.mapToOrderSummaryResponse(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public SellerOrderDetailResponse getSellerOrderDetail(String email, Long orderId) {
        shopRepository.findByUser_Email(email).orElseThrow(
                () -> new ResourceNotFoundException("Tài khoản chưa đăng ký mở Shop!"));

        Order order = orderRepository.findDetailByIdAndSellerEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc không thuộc quyền quản lý của Shop bạn!"));

        return orderMapper.mapToSellerOrderDetailResponse(order);
    }

    @Override
    @Transactional
    public SellerOrderDetailResponse confirmOrder(String email, Long orderId) {
        shopRepository.findByUser_Email(email).orElseThrow(
                () -> new ResourceNotFoundException("Tài khoản chưa đăng ký mở Shop!"));
        Order order = orderRepository.findDetailByIdAndSellerEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc không thuộc quyền quản lý của Shop bạn!"));
        if(!order.getStatus().canTransitionTo(StatusType.CONFIRMED)) {
            throw new BadRequestException("Không thể chuyển đơn hàng từ " + order.getStatus() + " sang " + StatusType.CONFIRMED);
        }
        order.setStatus(StatusType.CONFIRMED);
        orderRepository.save(order);
        return orderMapper.mapToSellerOrderDetailResponse(order);
    }

    @Override
    @Transactional
    public SellerOrderDetailResponse shipOrder(String email, Long orderId) {
        shopRepository.findByUser_Email(email).orElseThrow(
                () -> new ResourceNotFoundException("Tài khoản chưa đăng ký mở Shop!"));
        Order order = orderRepository.findDetailByIdAndSellerEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc không thuộc quyền quản lý của Shop bạn!"));
        if(!order.getStatus().canTransitionTo(StatusType.SHIPPING)) {
            throw new BadRequestException("Không thể chuyển đơn hàng từ " + order.getStatus() + " sang " + StatusType.SHIPPING);
        }
        order.setStatus(StatusType.SHIPPING);
        inventoryService.deliverOrder(order.getOrderItems());
        orderRepository.save(order);
        return orderMapper.mapToSellerOrderDetailResponse(order);
    }

    @Override
    @Transactional
    public SellerOrderDetailResponse deliverOrder(String email, Long orderId) {
        shopRepository.findByUser_Email(email).orElseThrow(
                () -> new ResourceNotFoundException("Tài khoản chưa đăng ký mở Shop!"));
        Order order = orderRepository.findDetailByIdAndSellerEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc không thuộc quyền quản lý của Shop bạn!"));
        if(!order.getStatus().canTransitionTo(StatusType.DELIVERED)) {
            throw new BadRequestException("Không thể chuyển đơn hàng từ " + order.getStatus() + " sang " + StatusType.DELIVERED);
        }
        order.setStatus(StatusType.DELIVERED);
        if (order.getPayment() != null && order.getPayment().getMethod() == PaymentMethod.COD) {
            order.getPayment().setPaidAt(LocalDateTime.now());
            order.getPayment().setStatus(PaymentStatus.COMPLETED);
        }

        orderRepository.save(order);
        return orderMapper.mapToSellerOrderDetailResponse(order);
    }
}
