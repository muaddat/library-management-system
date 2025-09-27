package com.book.librarymanagement.exception;

import org.springframework.security.access.AccessDeniedException;

public class InsufficientPermissionException extends AccessDeniedException {
    
    private static final long serialVersionUID = 1L;
    
    public InsufficientPermissionException(String message) {
        super(message);
    }
    
    public InsufficientPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}