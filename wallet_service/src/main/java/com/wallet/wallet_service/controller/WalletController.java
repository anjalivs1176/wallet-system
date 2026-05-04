package com.wallet.wallet_service.controller;

import java.util.*;
import com.wallet.wallet_service.dto.TransferRequest;
import com.wallet.wallet_service.entity.Transaction;
import com.wallet.wallet_service.entity.Wallet;
import com.wallet.wallet_service.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    private Long getUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createWallet(@RequestBody Map<String, Long> request) {

        Long userId = request.get("userId");

        walletService.createWallet(userId);

        return ResponseEntity.ok("Wallet created");
    }

    @GetMapping("/my-wallet")
    public ResponseEntity<Wallet> getWallet() {
        return ResponseEntity.ok(walletService.getWallet(getUserId()));
    }

    @PatchMapping("/add-balance")
    public ResponseEntity<Wallet> addBalance(@RequestParam Long amount) {
        return ResponseEntity.ok(walletService.addBalanceToWallet(getUserId(), amount));
    }

    @PatchMapping("/deduct-balance")
    public ResponseEntity<Wallet> deductBalance(@RequestParam Long amount) {
        return ResponseEntity.ok(walletService.deductBalanceFromWallet(getUserId(), amount));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<Transaction>> getHistory(
            @PageableDefault(size = 5, sort = "timestamp") Pageable pageable) {
        return ResponseEntity.ok(walletService.getTransactionHistory(getUserId(), pageable));
    }

    @Operation(summary = "Transfer money with idempotency support")
    @Parameter(
            name = "Idempotency-Key",
            description = "Unique key to prevent duplicate transactions",
            required = true,
            in = ParameterIn.HEADER
    )
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestHeader("Idempotency-Key") String key,
            @RequestBody TransferRequest request) {

        return ResponseEntity.ok(
                walletService.transferFunds(
                        key,
                        getUserId(),
                        request.getToUserId(),
                        request.getAmount()
                )
        );
    }
}
