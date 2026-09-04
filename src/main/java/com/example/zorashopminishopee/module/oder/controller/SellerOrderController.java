package com.example.zorashopminishopee.module.oder.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.common.dto.PageResponse;
import com.example.zorashopminishopee.module.oder.dto.response.OrderSummaryResponse;
import com.example.zorashopminishopee.module.oder.dto.response.SellerOrderDetailResponse;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/orders")
@AllArgsConstructor
public class SellerOrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getOrder(
            Authentication authentication,
            @RequestParam(required = false) StatusType status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<OrderSummaryResponse> responses = orderService.getOrderSummary(authentication.getName(), status, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(responses)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SellerOrderDetailResponse>> getOrderDetail(
            Authentication authentication,
            @PathVariable Long id) {
        SellerOrderDetailResponse response = orderService.getSellerOrderDetail(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<SellerOrderDetailResponse>> confirmOrder(
            Authentication authentication,
            @PathVariable Long id) {
        SellerOrderDetailResponse response = orderService.confirmOrder(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(response, "Xác nhận đơn hàng thành công!"));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<SellerOrderDetailResponse>> shipOrder(
            Authentication authentication,
            @PathVariable Long id) {
        SellerOrderDetailResponse response = orderService.shipOrder(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(response, "Đơn hàng đã được chuyển sang trạng thái giao hàng!"));
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<ApiResponse<SellerOrderDetailResponse>> deliverOrder(
            Authentication authentication,
            @PathVariable Long id) {
        SellerOrderDetailResponse response = orderService.deliverOrder(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(response, "Xác nhận giao hàng thành công!"));
    }

}
