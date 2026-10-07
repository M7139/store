package com.ga.store.dto;

import com.ga.store.enums.UserRole;
import com.ga.store.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserResponse {

    @Schema(example = "2")
    private Long id;

    @Schema(example = "Ahmed")
    private String firstName;

    @Schema(example = "Ali")
    private String lastName;

    @Schema(example = "user@example.com")
    private String email;

    @Schema(
            example = "CUSTOMER",
            allowableValues = {
                    "CUSTOMER",
                    "ADMIN"
            }
    )
    private UserRole role;

    @Schema(
            example = "ACTIVE",
            allowableValues = {
                    "ACTIVE",
                    "INACTIVE"
            }
    )
    private UserStatus status;

    @Schema(example = "true")
    private boolean verified;

    @Schema(example = "/uploads/profile-pictures/user-2.jpg")
    private String profilePictureUrl;

    public UserResponse() {
    }

    public UserResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            UserRole role,
            UserStatus status,
            boolean verified,
            String profilePictureUrl) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.verified = verified;
        this.profilePictureUrl = profilePictureUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}