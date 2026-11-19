package com.example.zorashopminishopee.module.catagory.dto.request;

final class CategorySlug {
    static final String PATTERN = "^[a-z0-9]+(-[a-z0-9]+)*$";
    static final String MESSAGE = "Slug must contain only lowercase letters, digits and single hyphens";

    private CategorySlug() {
    }
}
