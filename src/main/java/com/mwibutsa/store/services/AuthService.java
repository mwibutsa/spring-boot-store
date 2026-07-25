package com.mwibutsa.store.services;

import com.mwibutsa.store.dto.LoginRequest;
import com.mwibutsa.store.exceptions.UnAuthorizedException;
import com.mwibutsa.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(LoginRequest payload) {
        var user = userRepository.findByEmail(payload.getEmail()).orElse(null);

        if (user == null) {
            throw new UnAuthorizedException();
        }
        if (!passwordEncoder.matches(payload.getPassword(), user.getPassword())) {
            throw new UnAuthorizedException();
        }
    }
}
