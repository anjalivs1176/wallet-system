package com.wallet.wallet_service.consumer;

import com.wallet.wallet_service.event.UserCreatedEvent;
import com.wallet.wallet_service.service.WalletService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    private final WalletService walletService;

    public UserEventConsumer(WalletService walletService) {
        this.walletService = walletService;
    }

    @KafkaListener(topics = "user-created-topic", groupId = "wallet-group")
    public void consume(UserCreatedEvent event) {

        // create wallet automatically
        walletService.createWallet(Long.parseLong(event.getUserId()));
    }
}
