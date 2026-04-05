package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.ServiceImpl;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.TrackBalances;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.Transaction;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Enum.TransactionType;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Exceptions.BadRequestException;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Exceptions.ResourceNotFoundException;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Repository.TrackBalanceRepository;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service.TrackBalanceService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class TrackBalanceServiceImpl implements TrackBalanceService {

    TrackBalanceRepository trackBalanceRepository;


    public TrackBalanceServiceImpl(TrackBalanceRepository trackBalanceRepository) {
        this.trackBalanceRepository = trackBalanceRepository;
    }


    @Override
    public void createBalance(Transaction transaction) {
        Long userId = transaction.getUser().getId();


        Optional<TrackBalances> optionalWallet = trackBalanceRepository.findByUser_Id(userId);

        TrackBalances wallet;
        if (optionalWallet.isPresent()) {

            wallet = optionalWallet.get();
        } else {

            wallet = new TrackBalances();
            wallet.setUser(transaction.getUser());
            wallet.setNetBalance(BigDecimal.ZERO);
        }


        BigDecimal newBalance;
        if (transaction.getType() == TransactionType.INCOME) {
            newBalance = wallet.getNetBalance().add(transaction.getAmount());
        } else {
            newBalance = wallet.getNetBalance().subtract(transaction.getAmount());
        }


        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Insufficient funds! You only have ₹" + wallet.getNetBalance());
        }


        wallet.setNetBalance(newBalance);
        trackBalanceRepository.save(wallet);
    }

    @Override
    public BigDecimal getCurrentBalance(Long userId) {
        return trackBalanceRepository.findByUser_Id(userId)
                .map(TrackBalances::getNetBalance)
                .orElseThrow(() -> new ResourceNotFoundException("No balance record found for user ID: " + userId));
    }

    @Override
    public void updateBalanceOnCorrection(BigDecimal oldAmount, BigDecimal newAmount, TransactionType type, Long userId) {
        TrackBalances wallet = trackBalanceRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        BigDecimal difference = newAmount.subtract(oldAmount);
        BigDecimal currentBalance = wallet.getNetBalance();

         if (type == TransactionType.INCOME) {
            wallet.setNetBalance(currentBalance.add(difference));
        } else {
            wallet.setNetBalance(currentBalance.subtract(difference));
        }


        if (wallet.getNetBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Update failed: Insufficient funds for this correction.");
        }

        trackBalanceRepository.save(wallet);
    }

    @Override
    public void updateBalanceOnDeletion(Transaction transaction) {
        TrackBalances wallet = trackBalanceRepository.findByUser_Id(transaction.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));


        if (transaction.getType() == TransactionType.INCOME) {
            wallet.setNetBalance(wallet.getNetBalance().subtract(transaction.getAmount()));
        } else {
            wallet.setNetBalance(wallet.getNetBalance().add(transaction.getAmount()));
        }

        trackBalanceRepository.save(wallet);
    }
}
