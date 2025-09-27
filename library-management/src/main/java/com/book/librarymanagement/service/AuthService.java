package com.book.librarymanagement.service;

import com.book.librarymanagement.dto.AuthResponse;
import com.book.librarymanagement.dto.LoginRequest;
import com.book.librarymanagement.dto.SignUpRequest;
import com.book.librarymanagement.entity.User;
import com.book.librarymanagement.exception.UserException;

public interface AuthService {
    
    AuthResponse registerUser(SignUpRequest signUpRequest) throws UserException;
    
    AuthResponse authenticateUser(LoginRequest loginRequest) throws UserException;
    
    User findUserByUsername(String username) throws UserException;
}
