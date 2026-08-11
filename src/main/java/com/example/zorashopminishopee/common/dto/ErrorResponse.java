package com.example.zorashopminishopee.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private List<String> details;

    public static ErrorResponse of(int status, String error, String message) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .build();
    }

    public static ErrorResponse of(int status, String error, String message, List<String> details) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .details(details)
                .build();
    }
}
