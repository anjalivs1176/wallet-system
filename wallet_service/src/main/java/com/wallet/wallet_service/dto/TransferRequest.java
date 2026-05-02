package com.wallet.wallet_service.dto;

import lombok.Data;

@Data
public class TransferRequest {

    private Long toUserId;
    private Long amount;
}