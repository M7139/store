package com.ga.store.dto;

import com.ga.store.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class UserStatusRequest {

    @Schema(
            description = "New user account status",
            example = "INACTIVE",
            allowableValues = {
                    "ACTIVE",
                    "INACTIVE"
            }
    )
    @NotNull(message = "User status is required")
    private UserStatus status;

    public UserStatusRequest() {
    }

    public UserStatusRequest(
            UserStatus status) {

        this.status = status;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}