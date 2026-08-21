package com.example.zorashopminishopee.module.product.emun;

public enum ProductSortDir {
    ASC,
    DESC;
    public static ProductSortDir fromString(String value) {
        if (value == null || value.isBlank()) {
            return DESC;
        }
        try {
            return ProductSortDir.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DESC;
        }
    }
}
