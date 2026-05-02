package com.wallet.wallet_service.service;

import com.wallet.wallet_service.dto.TransferResponse;
import com.wallet.wallet_service.entity.*;
import com.wallet.wallet_service.exception.InsufficientBalanceException;
import com.wallet.wallet_service.exception.WalletNotFoundException;
import com.wallet.wallet_service.repository.IdempotencyRepository;
import com.wallet.wallet_service.repository.TransactionRepository;
import com.wallet.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRepository idempotencyRepository;

    @Transactional
    public Wallet createWallet(Long userId) {
        walletRepository.findByUserId(userId)
                .ifPresent(w -> {
                    throw new RuntimeException("Wallet already exists for this user");
                });

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(0L);

        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet getWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

    @Transactional
    public Wallet addBalanceToWallet(Long userId, Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + amount);
        Wallet savedWallet = walletRepository.save(wallet);

        logTransaction(userId, amount, TransactionType.CREDIT, TransactionStatus.SUCCESS);

        return savedWallet;
    }

    @Transactional
    public Wallet deductBalanceFromWallet(Long userId, Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        Wallet savedWallet = walletRepository.save(wallet);

        logTransaction(userId, amount, TransactionType.DEBIT, TransactionStatus.SUCCESS);

        return savedWallet;
    }

    public Page<Transaction> getTransactionHistory(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    @Transactional
    public TransferResponse transferFunds(String key, Long fromUserId, Long toUserId, Long amount) {

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Idempotency check
        Optional<IdempotencyRecord> existing = idempotencyRepository.findById(key);

        if (existing.isPresent()) {
            return new TransferResponse(
                    fromUserId,
                    toUserId,
                    amount,
                    existing.get().getResponse()
            );
        }

        Wallet sender = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found"));

        Wallet receiver = walletRepository.findByUserId(toUserId)
                .orElseThrow(() -> {
                    logTransaction(fromUserId, amount, TransactionType.DEBIT, TransactionStatus.FAILED);
                    return new WalletNotFoundException("Receiver wallet not found");
                });

        if (sender.getBalance() < amount) {
            logTransaction(fromUserId, amount, TransactionType.DEBIT, TransactionStatus.FAILED);
            throw new InsufficientBalanceException("Insufficient balance");
        }

        try {
            // Deduct and add
            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount);

            walletRepository.save(sender);
            walletRepository.save(receiver);

            // Log transactions
            logTransaction(fromUserId, amount, TransactionType.DEBIT, TransactionStatus.SUCCESS);
            logTransaction(toUserId, amount, TransactionType.CREDIT, TransactionStatus.SUCCESS);

            String response = "SUCCESS";

            // Save idempotency
            IdempotencyRecord record = new IdempotencyRecord();
            record.setIdempotencyKey(key);
            record.setResponse(response);
            record.setCreatedAt(LocalDateTime.now());

            idempotencyRepository.save(record);

            return new TransferResponse(
                    fromUserId,
                    toUserId,
                    amount,
                    response
            );

        } catch (Exception e) {

            // Log failure transaction
            logTransaction(fromUserId, amount, TransactionType.DEBIT, TransactionStatus.FAILED);

            throw e;
        }
    }

    private void logTransaction(Long userId, Long amount, TransactionType type, TransactionStatus status) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setStatus(status);
        transactionRepository.save(tx);
    }
}
