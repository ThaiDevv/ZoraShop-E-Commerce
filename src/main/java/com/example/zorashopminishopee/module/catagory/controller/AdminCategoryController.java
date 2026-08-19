package com.example.zorashopminishopee.module.catagory.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.catagory.dto.request.CreateCategoryRequest;
import com.example.zorashopminishopee.module.catagory.dto.response.CategoryResponse;
import com.example.zorashopminishopee.module.catagory.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {
    private final CategoryService categoryService;
    @PostMapping()
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        CategoryResponse newCategory = categoryService.createCategory(createCategoryRequest);
        return ResponseEntity.ok(ApiResponse.success(newCategory));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id
            ,@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        CategoryResponse newCategory = categoryService.updateCategory(id, createCategoryRequest);
        return ResponseEntity.ok(ApiResponse.success(newCategory));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
