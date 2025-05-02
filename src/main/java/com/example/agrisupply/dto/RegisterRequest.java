package com.example.agrisupply.dto;

import com.example.agrisupply.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    // Add other fields if needed during registration (e.g., name, phone)
    // private String name;
    // private String phone;

    @NotEmpty(message = "At least one role must be selected")
    private Set<Role> roles; // User must specify their role(s) during registration
}
