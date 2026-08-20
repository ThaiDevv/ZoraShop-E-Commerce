package com.example.zorashopminishopee.module.product.service;

import com.example.zorashopminishopee.module.product.dto.request.CreateProductRequest;
import com.example.zorashopminishopee.module.product.dto.response.ProductResponse;
import com.example.zorashopminishopee.module.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


public interface ProductService {
    ProductResponse createProduct(String email, CreateProductRequest request);
}
