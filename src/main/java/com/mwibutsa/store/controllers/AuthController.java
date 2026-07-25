package com.mwibutsa.store.controllers;

import com.mwibutsa.store.dto.LoginRequest;
import com.mwibutsa.store.exceptions.UnAuthorizedException;
import com.mwibutsa.store.repositories.UserRepository;
import com.mwibutsa.store.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest payload) {
        authService.login(payload);
        return ResponseEntity.ok(Map.of("message", "Login successful"));

    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<Map<String, String>> unAuthorizedHandler() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid user credentials"));
    }
}
