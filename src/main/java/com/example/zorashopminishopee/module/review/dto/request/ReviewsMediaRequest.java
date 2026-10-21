package com.example.zorashopminishopee.module.review.dto.request;

import com.example.zorashopminishopee.module.review.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ReviewsMediaRequest(
        @NotBlank @Size(max = 500) @URL(protocol = "https") String mediaUrl,
        @NotNull MediaType type
) {
}
