package com.example.zorashopminishopee.module.catagory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Partial update: null fields are left unchanged.
 * parentId = 0 moves the category to the root level.
 */
public record UpdateCategoryRequest(
        @PositiveOrZero Long parentId,
        @Size(min = 1, max = 255) String name,
        @Pattern(regexp = CategorySlug.PATTERN, message = CategorySlug.MESSAGE) String slug,
        @Size(max = 500) String iconUrl,
        @Min(0) Integer sortOrder)
{
}
