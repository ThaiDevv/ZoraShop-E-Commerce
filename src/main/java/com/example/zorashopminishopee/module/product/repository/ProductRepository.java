package com.example.zorashopminishopee.module.product.repository;

import com.example.zorashopminishopee.module.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

}
