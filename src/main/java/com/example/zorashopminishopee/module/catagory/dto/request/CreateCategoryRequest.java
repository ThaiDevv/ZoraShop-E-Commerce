package com.example.zorashopminishopee.module.catagory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @Positive Long parentId,
        @NotBlank(message = "Category name is required") @Size(max = 255) String name,
        @NotBlank(message = "Category slug is required")
        @Pattern(regexp = CategorySlug.PATTERN, message = CategorySlug.MESSAGE)
        String slug,
        @Size(max = 500) String iconUrl,
        @Min(0) Integer sortOrder)
{
}
