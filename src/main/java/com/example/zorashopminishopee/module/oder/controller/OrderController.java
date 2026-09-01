package com.example.zorashopminishopee.module.oder.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.common.dto.PageResponse;
import com.example.zorashopminishopee.module.oder.dto.request.CheckoutCartRequest;
import com.example.zorashopminishopee.module.oder.dto.response.CheckoutResponse;
import com.example.zorashopminishopee.module.oder.dto.response.HistoryOrderItemResponse;
import com.example.zorashopminishopee.module.oder.dto.response.HistoryOrderResponse;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckoutResponse>> createOrder(Authentication authentication,
                                                                     @Valid @RequestBody CheckoutCartRequest request) {
        CheckoutResponse response = orderService.orderFromCart(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HistoryOrderResponse>>> getHistoryOrder(Authentication authentication,
                                                                                           @RequestParam(required = false) StatusType status,
                                                                                           @RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "20") int size){
        Page<HistoryOrderResponse> responsePage = orderService.getHistoryOrders(authentication.getName(), status, page,size);
        return  ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(responsePage)));
    }
}
