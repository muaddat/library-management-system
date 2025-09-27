package com.book.librarymanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.book.librarymanagement.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private UUID userId;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime issuedAt;
    private String message;
    private boolean success;
    
    // Constructor for successful authentication
    public AuthResponse(String token, UUID userId, String username, String email, Role role, String message) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.issuedAt = LocalDateTime.now();
        this.message = message;
        this.success = true;
    }
    
    // Static method for successful registration response
    public static AuthResponse registrationSuccess(String token, UUID userId, String username, String email, Role role) {
        return new AuthResponse(token, userId, username, email, role, "User registered successfully");
    }
    
    // Static method for successful login response
    public static AuthResponse loginSuccess(String token, UUID userId, String username, String email, Role role) {
        return new AuthResponse(token, userId, username, email, role, "Login successful");
    }
}