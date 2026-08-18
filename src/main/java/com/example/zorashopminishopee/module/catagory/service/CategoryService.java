package com.example.zorashopminishopee.module.catagory.service;

import com.example.zorashopminishopee.module.catagory.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    public List<CategoryResponse> getCategoryTree();
}
