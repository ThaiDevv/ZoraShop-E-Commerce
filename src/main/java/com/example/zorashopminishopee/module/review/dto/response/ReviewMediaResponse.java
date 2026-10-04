package com.example.zorashopminishopee.module.review.dto.response;

import com.example.zorashopminishopee.module.review.enums.MediaType;

public record ReviewMediaResponse(
        String url,
        MediaType type
) {}
