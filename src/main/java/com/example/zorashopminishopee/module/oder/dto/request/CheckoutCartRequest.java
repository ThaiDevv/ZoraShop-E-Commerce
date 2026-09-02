package com.example.zorashopminishopee.module.oder.dto.request;

import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutCartRequest(
        @NotNull(message = "Địa chỉ giao hàng không được để trống")
        Long addressId,
        Long voucherId,
        @NotNull(message = "Vui lòng chọn phương thức thanh toán!")
        PaymentMethod  paymentMethod,

        String note,
        @NotEmpty(message = "Vui lòng chọn ít nhất 1 món hàng để đặt")
        List<Long> cartItemIds
) {
}
