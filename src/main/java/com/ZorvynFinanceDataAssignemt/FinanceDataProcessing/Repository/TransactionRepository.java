package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Repository;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.Transaction;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Enum.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 1. The missing method for Pagination and Filtering
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND t.deleted = false " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:type IS NULL OR t.type = :type)")
    Page<Transaction> findFilteredTransactions(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("type") TransactionType type,
            Pageable pageable);

    // 2. The Dashboard Summary method
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.deleted = false")
    BigDecimal sumAmountByUserIdAndType(
            @Param("userId") Long userId,
            @Param("type") TransactionType type);
}