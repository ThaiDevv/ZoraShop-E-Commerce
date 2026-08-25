package com.example.zorashopminishopee.module.product.service;

import com.example.zorashopminishopee.module.product.dto.request.CreateProductRequest;
import com.example.zorashopminishopee.module.product.dto.request.FilterSortRequest;
import com.example.zorashopminishopee.module.product.dto.request.UpdateProductRequest;
import com.example.zorashopminishopee.module.product.dto.response.ProductResponse;
import com.example.zorashopminishopee.module.product.dto.response.ProductSummaryResponse;
import com.example.zorashopminishopee.module.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


public interface ProductService {
    ProductResponse createProduct(String email, CreateProductRequest request);
    Page<ProductSummaryResponse> getAllProducts(FilterSortRequest request, int page, int size);
    ProductResponse getProduct(String slug);
    ProductResponse updateProduct(String email, String slug, UpdateProductRequest request);
    void deleteProduct(String email, String slug);
    Page<ProductSummaryResponse> getMyProducts(String email, int page, int size);
}
