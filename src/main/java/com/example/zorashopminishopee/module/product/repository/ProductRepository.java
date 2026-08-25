package com.example.zorashopminishopee.module.product.repository;

import com.example.zorashopminishopee.module.product.dto.response.ProductResponse;
import com.example.zorashopminishopee.module.product.dto.response.ProductSummaryResponse;
import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.users.entity.Shops;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

    Page<ProductResponse> findByShop_User_Email(String email,
                                                Pageable pageable);

    Page<Product> findByShop(Shops shop, Pageable pageable);
}
