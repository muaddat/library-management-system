package com.book.librarymanagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.librarymanagement.dto.BookRequest;
import com.book.librarymanagement.dto.BookResponse;
import com.book.librarymanagement.entity.Books;
import com.book.librarymanagement.exception.InsufficientPermissionException;
import com.book.librarymanagement.exception.UserException;
import com.book.librarymanagement.repository.BookRepository;
import com.book.librarymanagement.service.BookService;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    
    @Override
    public BookResponse createBook(BookRequest bookRequest) throws UserException {
        
        // Check if user has ADMIN role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new InsufficientPermissionException("Only administrators can create books");
        }
        
        // Validate available copies don't exceed total copies first
        if (bookRequest.getAvailableCopies() > bookRequest.getTotalCopies()) {
            throw new UserException("Available copies cannot exceed total copies");
        }
        
        // Check if ISBN already exists
        if (bookRequest.getIsbn() != null && bookRepository.existsByIsbn(bookRequest.getIsbn())) {
            throw new UserException("A book with this ISBN already exists");
        }
        
        Books book = new Books();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setDescription(bookRequest.getDescription());
        book.setTotalCopies(bookRequest.getTotalCopies());
        book.setAvailableCopies(bookRequest.getAvailableCopies());
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        
        Books savedBook = bookRepository.save(book);
        return mapToBookResponse(savedBook);
    }

    @Override
    public BookResponse updateBook(UUID bookId, BookRequest bookRequest) throws UserException {
        
        // Check if user has ADMIN role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new InsufficientPermissionException("Only administrators can update books");
        }
        
        Books existingBook = findBookEntityById(bookId);
        
        // Check if ISBN is being changed and if new ISBN already exists
        if (bookRequest.getIsbn() != null && 
            !bookRequest.getIsbn().equals(existingBook.getIsbn()) &&
            bookRepository.existsByIsbn(bookRequest.getIsbn())) {
            throw new UserException("A book with this ISBN already exists");
        }
        
        // Validate available copies don't exceed total copies
        if (bookRequest.getAvailableCopies() > bookRequest.getTotalCopies()) {
            throw new UserException("Available copies cannot exceed total copies");
        }
        
        existingBook.setTitle(bookRequest.getTitle());
        existingBook.setAuthor(bookRequest.getAuthor());
        existingBook.setIsbn(bookRequest.getIsbn());
        existingBook.setDescription(bookRequest.getDescription());
        existingBook.setTotalCopies(bookRequest.getTotalCopies());
        existingBook.setAvailableCopies(bookRequest.getAvailableCopies());
        existingBook.setUpdatedAt(LocalDateTime.now());
        
        Books updatedBook = bookRepository.save(existingBook);
        return mapToBookResponse(updatedBook);
    }

    @Override
    public void deleteBook(UUID bookId) throws UserException {
        
        // Check if user has ADMIN role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new InsufficientPermissionException("Only administrators can delete books");
        }
        
        Books book = findBookEntityById(bookId);
        bookRepository.delete(book);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> searchBooks(String searchTerm) {
        return bookRepository.findByTitleOrAuthorContainingIgnoreCase(searchTerm)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Books findBookEntityById(UUID bookId) throws UserException {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new UserException("Book not found with ID: " + bookId));
    }

    private BookResponse mapToBookResponse(Books book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .available(book.isAvailable())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}