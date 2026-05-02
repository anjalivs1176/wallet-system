package com.wallet.user_service.requestDto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}