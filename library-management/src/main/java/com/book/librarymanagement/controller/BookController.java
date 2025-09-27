package com.book.librarymanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.librarymanagement.dto.BookRequest;
import com.book.librarymanagement.dto.BookResponse;
import com.book.librarymanagement.exception.UserException;
import com.book.librarymanagement.service.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<BookResponse>> getAllBooks(@RequestParam(required = false) String search) {
        List<BookResponse> books;
        
        if (search != null && !search.trim().isEmpty()) {
            books = bookService.searchBooks(search.trim());
        } else {
            books = bookService.getAllBooks();
        }
        
        return ResponseEntity.ok(books);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) 
            throws UserException {
        BookResponse createdBook = bookService.createBook(bookRequest);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }


    @PutMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(@PathVariable UUID bookId, 
                                                  @Valid @RequestBody BookRequest bookRequest) 
            throws UserException {
        BookResponse updatedBook = bookService.updateBook(bookId, bookRequest);
        return ResponseEntity.ok(updatedBook);
    }


    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID bookId) throws UserException {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}