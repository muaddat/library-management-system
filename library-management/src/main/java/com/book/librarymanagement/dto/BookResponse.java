package com.book.librarymanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {
    
    private UUID bookId;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private Integer totalCopies;
    private Integer availableCopies;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}