package com.example.zorashopminishopee.module.catagory.dto.request;

import com.example.zorashopminishopee.module.catagory.entity.Category;

import java.util.ArrayList;
import java.util.List;

public record CreateCategoryRequest(
        Long parentId,
        String name,
        String slug,
        String iconUrl,
        Integer sortOrder)
{
}
