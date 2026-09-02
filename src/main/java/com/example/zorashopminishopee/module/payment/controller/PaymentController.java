package com.example.zorashopminishopee.module.payment.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.payment.dto.response.PaymentResponse;
import com.example.zorashopminishopee.module.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            Authentication authentication,
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.processPayment(authentication.getName(), orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatus(
            Authentication authentication,
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentStatus(authentication.getName(), orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
