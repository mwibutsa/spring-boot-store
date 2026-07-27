package com.mwibutsa.store.controllers;

import com.mwibutsa.store.config.JwtConfig;
import com.mwibutsa.store.dto.JwtResponse;
import com.mwibutsa.store.dto.LoginRequest;
import com.mwibutsa.store.dto.UserDto;
import com.mwibutsa.store.mappers.UserMapper;
import com.mwibutsa.store.repositories.UserRepository;
import com.mwibutsa.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private JwtConfig jwtConfig;

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshAccessToken(@CookieValue(value = "refreshToken") String refreshToken) {

        var jwt = jwtService.parseToken(refreshToken);

        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(jwt.getUserid()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest payload,
                                             HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getEmail(), payload.getPassword())
        );

        var user = userRepository.findByEmail(payload.getEmail()).orElseThrow();

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var cookie = new Cookie("refreshToken", refreshToken.toString());

        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // 7d
        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));

    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        var userId = (Long) authentication.getPrincipal();
        assert userId != null;
        var user = userRepository.findById(userId).orElse(null);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> unAuthorizedHandler() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid user credentials"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> exceptionHandler(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", exception.getMessage()));
    }

}
