package com.book.librarymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.book.librarymanagement.dto.AuthResponse;
import com.book.librarymanagement.dto.LoginRequest;
import com.book.librarymanagement.dto.SignUpRequest;
import com.book.librarymanagement.exception.UserException;
import com.book.librarymanagement.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) 
            throws UserException {
        
        AuthResponse authResponse = authService.registerUser(signUpRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) 
            throws UserException {
        
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
