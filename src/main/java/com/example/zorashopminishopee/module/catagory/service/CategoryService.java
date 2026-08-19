package com.example.zorashopminishopee.module.catagory.service;

import com.example.zorashopminishopee.module.catagory.dto.request.CreateCategoryRequest;
import com.example.zorashopminishopee.module.catagory.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    public List<CategoryResponse> getCategoryTree();
    public CategoryResponse createCategory(CreateCategoryRequest request);
    public CategoryResponse updateCategory(Long id, CreateCategoryRequest request);
    public void deleteCategory(Long id);
}
