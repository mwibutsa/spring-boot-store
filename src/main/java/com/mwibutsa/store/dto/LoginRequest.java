package com.mwibutsa.store.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Emails should not be empty")
    private String email;

    @NotNull(message = "Password should be provided")
    @NotEmpty(message = "Password should not be empty")
    private String password;
}
