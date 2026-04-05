package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    // Custom constructor so you don't have to manually pass LocalDateTime.now() every time
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
}