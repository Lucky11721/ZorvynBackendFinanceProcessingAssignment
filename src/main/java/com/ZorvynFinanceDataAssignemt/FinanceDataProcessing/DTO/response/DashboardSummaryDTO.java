package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response;

import java.math.BigDecimal;

public record DashboardSummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal currentNetBalance
) {
}