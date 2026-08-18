package com.example.zorashopminishopee.module.catagory.repository;

import com.example.zorashopminishopee.module.catagory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByIsActiveTrueOrderBySortOrderAsc();
}
