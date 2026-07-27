package com.mwibutsa.store.services;

import com.mwibutsa.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@AllArgsConstructor
@Service
public class AuthService implements UserDetailsService {


    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }

    public com.mwibutsa.store.entities.User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        var userId = (Long) authentication.getPrincipal();
        assert userId != null;
        return userRepository.findById(userId).orElseThrow();

    }
}
