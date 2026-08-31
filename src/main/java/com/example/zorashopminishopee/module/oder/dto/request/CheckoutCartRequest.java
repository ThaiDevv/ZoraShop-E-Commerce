package com.example.zorashopminishopee.module.oder.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutCartRequest(
        @NotNull(message = "Địa chỉ giao hàng không được để trống")
        Long addressId,
        Long voucherId,
        String note,
        @NotEmpty(message = "Vui lòng chọn ít nhất 1 món hàng để đặt")
        List<Long> cartItemIds
) {
}
