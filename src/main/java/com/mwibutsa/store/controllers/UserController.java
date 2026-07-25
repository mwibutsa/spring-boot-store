package com.mwibutsa.store.controllers;

import com.mwibutsa.store.dto.ChangePasswordRequest;
import com.mwibutsa.store.dto.RegisterUserRequest;
import com.mwibutsa.store.dto.UpdateUserRequest;
import com.mwibutsa.store.dto.UserDto;
import com.mwibutsa.store.entities.User;
import com.mwibutsa.store.mappers.UserMapper;
import com.mwibutsa.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "", name = "sort") String sort) {
        if (!Set.of("name", "email").contains(sort)) {
            sort = "name";
        }
        return userRepository.
                findAll(Sort.by(sort)).
                stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest payload,
            UriComponentsBuilder uriBuilder) {

        if (userRepository.existsByEmail(payload.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("email", "Email is already registered"));
        }

        User user = userMapper.toEntity(payload);
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        userRepository.save(user);

        UserDto userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest payload) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.updateUser(payload, user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<UserDto> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest payload) {

        IO.println("Request to change password!");
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (user.getPassword().equals(payload.getNewPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userMapper.changePassword(payload, user);
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }


}
