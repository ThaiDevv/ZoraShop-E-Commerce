package com.example.zorashopminishopee.module.review.dto.request;

import com.example.zorashopminishopee.module.review.enums.MediaType;

public record ReviewsMediaRequest(
        String mediaUrl,
        MediaType type
){ }
