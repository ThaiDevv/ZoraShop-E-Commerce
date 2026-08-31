package com.example.zorashopminishopee.module.product.enums;

public enum ProductSortBy {
    CREATED_DATE("createdDate"),
    PRICE("price"),
    SOLD_COUNT("soldCount"),
    RATING_AVG("ratingAvg");
    private final String fieldName;
    ProductSortBy(String fieldName) {
        this.fieldName = fieldName;
    }
    public String getFieldName() {
        return fieldName;
    }
    public static String getValidFieldName(String value) {
        if (value == null || value.isBlank()) {
            return CREATED_DATE.getFieldName();
        }
        for (ProductSortBy sort : values()) {
            if (sort.name().equalsIgnoreCase(value) || sort.getFieldName().equalsIgnoreCase(value)) {
                return sort.getFieldName();
            }
        }
        return CREATED_DATE.getFieldName();
    }
}
