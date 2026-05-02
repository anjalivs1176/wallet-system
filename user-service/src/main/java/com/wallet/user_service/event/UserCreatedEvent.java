package com.wallet.user_service.event;

public class UserCreatedEvent {

    private String userId;
    private String email;
    private String createdAt;

    public UserCreatedEvent() {}

    public UserCreatedEvent(String userId, String email, String createdAt) {
        this.userId = userId;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
