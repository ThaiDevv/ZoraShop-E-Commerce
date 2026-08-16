package com.example.zorashopminishopee.module.users.dto.response;

public record ShopResponse(
        Long id,
        String name,
        String description,
        String logoUrl,
        String bannerUrl,
        Double rating,
        Integer totalProducts,
        Integer totalFollowers,
        Boolean isActive
){
}
