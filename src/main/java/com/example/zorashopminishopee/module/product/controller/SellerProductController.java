package com.example.zorashopminishopee.module.product.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.product.dto.request.CreateProductRequest;
import com.example.zorashopminishopee.module.product.dto.response.ProductResponse;
import com.example.zorashopminishopee.module.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(Authentication authentication,@Valid @RequestBody CreateProductRequest request){
        ProductResponse productResponse = productService.createProduct(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(productResponse));
    }
}
