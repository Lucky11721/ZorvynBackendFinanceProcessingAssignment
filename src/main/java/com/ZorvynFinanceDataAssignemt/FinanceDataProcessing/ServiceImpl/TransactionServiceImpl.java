package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.ServiceImpl;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request.TransactionRequestDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.DashboardSummaryDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.TransactionalResponseDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.Transaction;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.User;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Enum.TransactionType;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Exceptions.BadRequestException;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Exceptions.ResourceNotFoundException;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Repository.TransactionRepository;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Repository.UserRepository;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service.TrackBalanceService;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TrackBalanceService trackBalanceService;

    @Override
    @Transactional
    public TransactionalResponseDTO addTransaction(Long userId, TransactionRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(request.amount())
                .type(TransactionType.valueOf(request.type().toUpperCase()))
                .category(request.category())
                .notes(request.notes())
                .deleted(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        trackBalanceService.createBalance(saved);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public TransactionalResponseDTO updateTransaction(Long transactionId, TransactionRequestDTO request) {
        Transaction existing = transactionRepository.findById(transactionId)
                .filter(t -> !t.isDeleted()) // SECURITY: Prevent updating deleted records
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or has been deleted"));


        trackBalanceService.updateBalanceOnCorrection(
                existing.getAmount(),
                request.amount(),
                existing.getType(),
                existing.getUser().getId()
        );

        existing.setAmount(request.amount());
        existing.setCategory(request.category());
        existing.setNotes(request.notes());

        return mapToDTO(transactionRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId) {
        Transaction existing = transactionRepository.findById(transactionId)
                .filter(t -> !t.isDeleted()) // SECURITY: Prevent deleting already deleted records
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or has been deleted"));


        trackBalanceService.updateBalanceOnDeletion(existing);


        existing.setDeleted(true);
        transactionRepository.save(existing);
    }

    @Override
    public TransactionalResponseDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .filter(t -> !t.isDeleted()) // Hide deleted records
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
        return mapToDTO(transaction);
    }

    @Override
    public Page<TransactionalResponseDTO> getAllUserTransactions(Long userId, String category, String type, Pageable pageable) {


        TransactionType transactionType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                transactionType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid transaction type. Use INCOME or EXPENSE.");
            }
        }

        return transactionRepository.findFilteredTransactions(userId, category, transactionType, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary(Long userId) {

        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        BigDecimal netBalance = trackBalanceService.getCurrentBalance(userId);

        return new DashboardSummaryDTO(
                totalIncome != null ? totalIncome : BigDecimal.ZERO,
                totalExpenses != null ? totalExpenses : BigDecimal.ZERO,
                netBalance
        );
    }

    @Override
    @Transactional
    public TransactionalResponseDTO restoreTransaction(Long transactionId) {

        Transaction existing = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));


        if (!existing.isDeleted()) {
            throw new BadRequestException("This transaction is already active and cannot be restored.");
        }


        existing.setDeleted(false);


        trackBalanceService.createBalance(existing);


        return mapToDTO(transactionRepository.save(existing));
    }

    private TransactionalResponseDTO mapToDTO(Transaction t) {
        return new TransactionalResponseDTO(
                t.getId(),
                t.getAmount(),
                t.getType().name(),
                t.getCategory(),
                t.getNotes(),
                t.getDate()
        );
    }
}