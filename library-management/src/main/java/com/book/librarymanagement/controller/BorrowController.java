package com.book.librarymanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.librarymanagement.dto.BorrowRequest;
import com.book.librarymanagement.dto.BorrowResponse;
import com.book.librarymanagement.exception.UserException;
import com.book.librarymanagement.service.BorrowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;


    @PostMapping("/borrow")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BorrowResponse> borrowBook(@Valid @RequestBody BorrowRequest borrowRequest, 
                                                    Principal principal) throws UserException {
        String username = principal.getName();
        BorrowResponse response = borrowService.borrowBook(username, borrowRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    
    @PostMapping("/return")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BorrowResponse> returnBook(@RequestParam UUID bookId, 
                                                    Principal principal) throws UserException {
        String username = principal.getName();
        BorrowResponse response = borrowService.returnBook(username, bookId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<BorrowResponse>> getBorrowHistory(Principal principal) throws UserException {
        String username = principal.getName();
        List<BorrowResponse> history = borrowService.getUserBorrowHistory(username);
        return ResponseEntity.ok(history);
    }

}