package com.example.zorashopminishopee.module.product.repository;

import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    boolean existsBySkuIn(List<String> skus);
}
