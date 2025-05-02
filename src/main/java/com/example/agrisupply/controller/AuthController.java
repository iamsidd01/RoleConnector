package com.example.agrisupply.controller;

import com.example.agrisupply.dto.JwtAuthenticationResponse;
import com.example.agrisupply.dto.LoginRequest;
import com.example.agrisupply.dto.RegisterRequest;
import com.example.agrisupply.model.User;
import com.example.agrisupply.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User result = authService.registerUser(registerRequest);
            // Consider returning only necessary info, not the full user object
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
            // Or return a DTO like: return ResponseEntity.ok(new UserDto(result.getId(), result.getEmail(), result.getRoles()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String jwt = authService.loginUser(loginRequest);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (Exception e) { // Catch specific authentication exceptions if needed
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }
}
