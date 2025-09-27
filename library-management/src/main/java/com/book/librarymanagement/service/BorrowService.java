package com.book.librarymanagement.service;

import java.util.List;
import java.util.UUID;

import com.book.librarymanagement.dto.BorrowRequest;
import com.book.librarymanagement.dto.BorrowResponse;
import com.book.librarymanagement.exception.UserException;

public interface BorrowService {
    
    BorrowResponse borrowBook(String username, BorrowRequest borrowRequest) throws UserException;
    
    BorrowResponse returnBook(String username, UUID bookId) throws UserException;
    
    List<BorrowResponse> getUserBorrowHistory(String username) throws UserException;
}