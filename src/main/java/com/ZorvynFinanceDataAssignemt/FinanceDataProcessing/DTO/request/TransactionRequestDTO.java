package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Transaction type is required")
        String type, // Expected: "INCOME" or "EXPENSE"

        @NotBlank(message = "Category is required")
        String category,

        String notes
) {
}