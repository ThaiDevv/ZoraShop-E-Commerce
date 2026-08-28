package com.example.zorashopminishopee.module.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCartItemRequest(
        @NotBlank(message = "Mã SKU không được để trống")
        String sku,

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
        Integer quantity
) {}

