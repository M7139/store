package com.ga.store.controller;

import com.ga.store.dto.ChangePasswordRequest;
import com.ga.store.dto.UpdateProfileRequest;
import com.ga.store.dto.UserResponse;
import com.ga.store.dto.UserStatusRequest;
import com.ga.store.model.User;
import com.ga.store.service.AuditLogService;
import com.ga.store.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    public UserController(
            UserService userService,
            AuditLogService auditLogService) {

        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        User user =
                userService.getUserByEmail(email);

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        User user =
                userService.updateProfile(
                        email,
                        request
                );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PostMapping(
            value = "/me/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponse> uploadProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        String email = authentication.getName();

        User user =
                userService.uploadProfilePicture(
                        email,
                        file
                );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();

        userService.changePassword(
                email,
                request
        );

        return new ResponseEntity<>(
                "Password changed successfully",
                HttpStatus.OK
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response =
                userService.getAllUsers()
                        .stream()
                        .map(this::createUserResponse)
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId) {

        User user =
                userService.getUserById(
                        userId
                );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/admin/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            Authentication authentication,
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusRequest request) {

        User admin =
                userService.getUserByEmail(
                        authentication.getName()
                );

        User user =
                userService.updateUserStatus(
                        userId,
                        request
                );

        auditLogService.createAuditLog(
                admin.getId(),
                "USER_STATUS_CHANGED",
                "Changed user "
                        + user.getId()
                        + " status to "
                        + user.getStatus()
        );

        UserResponse response =
                createUserResponse(user);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    private UserResponse createUserResponse(
            User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.isVerified(),
                user.getProfilePictureUrl()
        );
    }
}