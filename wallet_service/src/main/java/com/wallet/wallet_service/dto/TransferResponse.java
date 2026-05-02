package com.wallet.wallet_service.dto;

import lombok.Data;

@Data
public class TransferResponse {
    private Long fromUserId;
    private Long toUserId;
    private Long amount;
    private String status;

    public TransferResponse(Long fromUserId, Long toUserId, Long amount, String status) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.status = status;
    }
}