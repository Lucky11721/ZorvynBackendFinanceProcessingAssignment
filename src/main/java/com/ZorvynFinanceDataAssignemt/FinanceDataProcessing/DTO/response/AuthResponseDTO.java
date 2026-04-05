package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response;

public record AuthResponseDTO(
        String token,
        String type,
        Long id,
        String email,
        String role
) {
    // Custom constructor to automatically set the type to "Bearer"
    public AuthResponseDTO(String token, Long id, String email, String role) {
        this(token, "Bearer", id, email, role);
    }
}