package com.example.zorashopminishopee.module.users.dto.response;

import java.time.LocalDateTime;

public record AddressResponse(
        Long id,
        String fullName,
        String phone,
        String street,
        String ward,
        String district,
        String city,
        Boolean isDefault,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {}
