package com.example.zorashopminishopee.module.review.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateReviewRequest(
        @NotNull @Positive Long orderItemId,
        @NotNull @Min(1) @Max(5) Integer star,
        @Size(max = 2000) String comment,
        boolean isAnonymous,
        @Size(max = 5) List<@NotNull @Valid ReviewsMediaRequest> medias
) {
}
