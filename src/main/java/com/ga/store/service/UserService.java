package com.ga.store.service;

import com.ga.store.dto.LoginRequest;
import com.ga.store.dto.RegisterRequest;
import com.ga.store.enums.UserStatus;
import com.ga.store.exception.EmailNotVerifiedException;
import com.ga.store.exception.InactiveAccountException;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InvalidCredentialsException;
import com.ga.store.model.User;
import com.ga.store.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationService emailVerificationService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InformationExistsException("User with this email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                hashedPassword
        );

        User savedUser = userRepository.save(user);

        emailVerificationService.createVerificationToken(savedUser);

        return savedUser;
    }

    public User loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new InactiveAccountException("Account is inactive");
        }

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException("Email is not verified");
        }

        return user;
    }
}