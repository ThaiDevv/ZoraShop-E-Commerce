package com.example.zorashopminishopee.module.product.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.common.dto.PageResponse;
import com.example.zorashopminishopee.module.product.dto.request.FilterSortRequest;
import com.example.zorashopminishopee.module.product.dto.response.ProductResponse;
import com.example.zorashopminishopee.module.product.dto.response.ProductSummaryResponse;
import com.example.zorashopminishopee.module.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getAllProducts(
            @ModelAttribute FilterSortRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ProductSummaryResponse> productPage = productService.getAllProducts(request, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(productPage)));
    }
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        ProductResponse productResponse = productService.getProduct(slug);
        return ResponseEntity.ok(ApiResponse.success(productResponse));
    }
}

