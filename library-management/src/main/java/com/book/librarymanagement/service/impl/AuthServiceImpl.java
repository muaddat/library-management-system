package com.book.librarymanagement.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.book.librarymanagement.dto.AuthResponse;
import com.book.librarymanagement.dto.LoginRequest;
import com.book.librarymanagement.dto.SignUpRequest;
import com.book.librarymanagement.entity.User;
import com.book.librarymanagement.enums.Role;
import com.book.librarymanagement.exception.UserException;
import com.book.librarymanagement.repository.UserRepository;
import com.book.librarymanagement.security.JwtProvider;
import com.book.librarymanagement.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public AuthResponse registerUser(SignUpRequest signUpRequest) throws UserException {
        
        // Check if username already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserException("Username is already taken!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserException("Email is already in use!");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        
        // Set role
        try {
            user.setRole(Role.valueOf(signUpRequest.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER); // Default to USER if invalid role
        }
        
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        // Generate JWT token for the new user
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            savedUser.getUsername(),
            signUpRequest.getPassword()
        );
        String token = jwtProvider.generateToken(authentication);
        
        // Return AuthResponse with token and user details
        return AuthResponse.registrationSuccess(
            token,
            savedUser.getUserId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
    }

    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) throws UserException {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // Generate JWT token
            String token = jwtProvider.generateToken(authentication);
            
            // Get user details
            User user = findUserByUsername(loginRequest.getUsername());
            
            // Return AuthResponse with token and user details
            return AuthResponse.loginSuccess(
                token,
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
            );
            
        } catch (AuthenticationException e) {
            throw new UserException("Invalid username or password");
        }
    }

    @Override
    public User findUserByUsername(String username) throws UserException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserException("User not found with username: " + username);
        }
        return user;
    }
}
