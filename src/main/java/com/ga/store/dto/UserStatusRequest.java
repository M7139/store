package com.ga.store.dto;

import com.ga.store.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public class UserStatusRequest {

    @NotNull(message = "User status is required")
    private UserStatus status;

    public UserStatusRequest() {
    }

    public UserStatusRequest(UserStatus status) {
        this.status = status;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}