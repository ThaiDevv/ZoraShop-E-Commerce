package com.example.zorashopminishopee.module.catagory.dto.response;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String iconUrl,
        int level,
        int sortOrder,
        List<CategoryResponse> children
) {
    public CategoryResponse {
        if (children == null) {
            children = new ArrayList<>();
        }
    }
}

