package com.example.zorashopminishopee.module.product.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.common.dto.PageResponse;
import com.example.zorashopminishopee.module.product.dto.request.CreateProductRequest;
import com.example.zorashopminishopee.module.product.dto.request.UpdateProductRequest;
import com.example.zorashopminishopee.module.product.dto.response.ProductResponse;
import com.example.zorashopminishopee.module.product.dto.response.ProductSummaryResponse;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import com.example.zorashopminishopee.module.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {
    private final ProductService productService;
    private final InventoryService inventoryService;
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(Authentication authentication,@Valid @RequestBody CreateProductRequest request){
        ProductResponse productResponse = productService.createProduct(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(productResponse));
    }
    @PutMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(Authentication authentication, @PathVariable String slug, @Valid @RequestBody UpdateProductRequest request){
        ProductResponse productResponse = productService.updateProduct(authentication.getName(), slug, request);
        return ResponseEntity.ok(ApiResponse.success(productResponse));
    }
    @DeleteMapping("/{slug}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(Authentication authentication, @PathVariable String slug){
        productService.deleteProduct(authentication.getName(), slug);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getMyProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ProductSummaryResponse> responses = productService.getMyProducts(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(responses)));
    }

}
