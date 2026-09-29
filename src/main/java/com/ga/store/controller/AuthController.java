package com.ga.store.controller;

import com.ga.store.dto.RegisterRequest;
import com.ga.store.dto.UserResponse;
import com.ga.store.model.User;
import com.ga.store.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {

        User user = userService.registerUser(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.isVerified()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}