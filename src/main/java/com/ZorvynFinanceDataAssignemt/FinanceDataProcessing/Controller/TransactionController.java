package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Controller;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request.TransactionRequestDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.DashboardSummaryDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.TransactionalResponseDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Added for Pagination
import org.springframework.data.domain.Pageable; // Added for Pagination
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/user/{userId}")
    public ResponseEntity<TransactionalResponseDTO> addTransaction(
            @PathVariable Long userId,
            @Valid @RequestBody TransactionRequestDTO request) {
        return new ResponseEntity<>(transactionService.addTransaction(userId, request), HttpStatus.CREATED);
    }


    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionalResponseDTO> getTransactionById(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }


    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionalResponseDTO> updateTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionRequestDTO request) {
        return ResponseEntity.ok(transactionService.updateTransaction(transactionId, request));
    }


    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TransactionalResponseDTO>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            Pageable pageable) { // Spring automatically maps ?page=0&size=10 to this object

        return ResponseEntity.ok(transactionService.getAllUserTransactions(userId, category, type, pageable));
    }


    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getDashboardSummary(userId));
    }



    @PatchMapping("/{transactionId}/restore")
    public ResponseEntity<TransactionalResponseDTO> restoreTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.restoreTransaction(transactionId));
    }
}