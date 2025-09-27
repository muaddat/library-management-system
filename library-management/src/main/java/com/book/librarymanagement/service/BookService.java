package com.book.librarymanagement.service;

import java.util.List;
import java.util.UUID;

import com.book.librarymanagement.dto.BookRequest;
import com.book.librarymanagement.dto.BookResponse;
import com.book.librarymanagement.entity.Books;
import com.book.librarymanagement.exception.UserException;

public interface BookService {
    
    BookResponse createBook(BookRequest bookRequest) throws UserException;
    
    BookResponse updateBook(UUID bookId, BookRequest bookRequest) throws UserException;
    
    void deleteBook(UUID bookId) throws UserException;
    
    List<BookResponse> getAllBooks();
    
    List<BookResponse> searchBooks(String searchTerm);
    
    Books findBookEntityById(UUID bookId) throws UserException;
}