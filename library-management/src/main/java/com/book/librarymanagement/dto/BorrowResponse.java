package com.book.librarymanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.book.librarymanagement.enums.BorrowStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowResponse {
    
    private UUID borrowId;
    private UUID userId;
    private String username;
    private UUID bookId;
    private String bookTitle;
    private String bookAuthor;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BorrowStatus status;
    private String message;
    private boolean success;
    
    public static BorrowResponse success(UUID borrowId, UUID userId, String username, 
                                       UUID bookId, String bookTitle, String bookAuthor,
                                       LocalDateTime borrowDate, LocalDateTime dueDate, 
                                       BorrowStatus status, String message) {
        return BorrowResponse.builder()
                .borrowId(borrowId)
                .userId(userId)
                .username(username)
                .bookId(bookId)
                .bookTitle(bookTitle)
                .bookAuthor(bookAuthor)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(status)
                .message(message)
                .success(true)
                .build();
    }
}