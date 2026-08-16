package com.example.zorashopminishopee.module.users.dto.request;

public record CreateAddressRequest(
        String fullName,
        String phone,
        String street,
        String ward,
        String district,
        String city,
        Boolean isDefault
) {}
