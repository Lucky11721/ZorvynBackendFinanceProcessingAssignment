package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.Transaction;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Enum.TransactionType;

import java.math.BigDecimal;

public interface TrackBalanceService {
    void createBalance(Transaction transaction);
    BigDecimal getCurrentBalance(Long userId);
    // Add to TrackBalanceService.java
    void updateBalanceOnCorrection(BigDecimal oldAmount, BigDecimal newAmount, TransactionType type, Long userId);
    void updateBalanceOnDeletion(Transaction transaction);
}