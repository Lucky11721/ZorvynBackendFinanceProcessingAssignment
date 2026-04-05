package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionalResponseDTO(
        Long id,
        BigDecimal amount,
        String type,
        String category,
        String notes,
        LocalDateTime createdAt
) {
}