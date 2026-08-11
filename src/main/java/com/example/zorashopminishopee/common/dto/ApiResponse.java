package com.example.zorashopminishopee.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ApiResponse<T> {
    private String message;
    private Boolean success;
    private T body;
    private LocalDateTime timestamp;
    public static <T> ApiResponse<T> success(T body, String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .success(true)
                .body(body)
                .build();
    }
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation successful")
                .body(data)
                .build();
    }
    public static <T> ApiResponse<T> error (T body, String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .success(false)
                .body(body)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
