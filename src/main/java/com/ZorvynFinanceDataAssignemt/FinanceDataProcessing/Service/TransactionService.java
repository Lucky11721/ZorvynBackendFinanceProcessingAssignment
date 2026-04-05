package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request.TransactionRequestDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.DashboardSummaryDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.TransactionalResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

  TransactionalResponseDTO addTransaction(Long userId, TransactionRequestDTO request);
  TransactionalResponseDTO updateTransaction(Long transactionId, TransactionRequestDTO request);
  void deleteTransaction(Long transactionId);


  TransactionalResponseDTO getTransactionById(Long transactionId);

  Page<TransactionalResponseDTO> getAllUserTransactions(Long userId, String category, String type, Pageable pageable);


  DashboardSummaryDTO getDashboardSummary(Long userId);



  TransactionalResponseDTO restoreTransaction(Long transactionId);

}