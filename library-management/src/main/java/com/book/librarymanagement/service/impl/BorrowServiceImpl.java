package com.book.librarymanagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.librarymanagement.dto.BorrowRequest;
import com.book.librarymanagement.dto.BorrowResponse;
import com.book.librarymanagement.entity.Books;
import com.book.librarymanagement.entity.BorrowRecord;
import com.book.librarymanagement.entity.User;
import com.book.librarymanagement.enums.BorrowStatus;
import com.book.librarymanagement.exception.UserException;
import com.book.librarymanagement.repository.BorrowRecordRepository;
import com.book.librarymanagement.service.AuthService;
import com.book.librarymanagement.service.BookService;
import com.book.librarymanagement.service.BorrowService;

@Service
@Transactional
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthService authService;

    @Override
    public BorrowResponse borrowBook(String username, BorrowRequest borrowRequest) throws UserException {
        
        User user = authService.findUserByUsername(username);
        Books book = bookService.findBookEntityById(borrowRequest.getBookId());
        
        // Check if book is available
        if (!book.isAvailable()) {
            throw new UserException("Book is not available for borrowing");
        }
        
        // Check if user already has this book borrowed
        if (borrowRecordRepository.existsByUserAndBookBookIdAndStatus(user, book.getBookId(), BorrowStatus.BORROWED)) {
            throw new UserException("You have already borrowed this book");
        }
        
        // Create borrow record
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        borrowRecord.setDueDate(LocalDateTime.now().plusDays(14)); // 2 weeks loan period
        borrowRecord.setStatus(BorrowStatus.BORROWED);
        borrowRecord.setCreatedAt(LocalDateTime.now());
        borrowRecord.setUpdatedAt(LocalDateTime.now());
        
        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setUpdatedAt(LocalDateTime.now());
        
        // Save both records
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        
        return BorrowResponse.success(
            savedRecord.getBorrowId(),
            user.getUserId(),
            user.getUsername(),
            book.getBookId(),
            book.getTitle(),
            book.getAuthor(),
            savedRecord.getBorrowDate(),
            savedRecord.getDueDate(),
            savedRecord.getStatus(),
            "Book borrowed successfully"
        );
    }

    @Override
    public BorrowResponse returnBook(String username, UUID bookId) throws UserException {
        
        User user = authService.findUserByUsername(username);
        Books book = bookService.findBookEntityById(bookId);
        
        // Find active borrow record
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByUserAndBookAndStatus(
            user, bookId, BorrowStatus.BORROWED);
        
        if (borrowRecords.isEmpty()) {
            throw new UserException("No active borrow record found for this book");
        }
        
        BorrowRecord borrowRecord = borrowRecords.get(0);
        
        // Update borrow record
        borrowRecord.setReturnDate(LocalDateTime.now());
        borrowRecord.setStatus(BorrowStatus.RETURNED);
        borrowRecord.setUpdatedAt(LocalDateTime.now());
        
        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setUpdatedAt(LocalDateTime.now());
        
        // Save both records
        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);
        
        BorrowResponse response = BorrowResponse.success(
            updatedRecord.getBorrowId(),
            user.getUserId(),
            user.getUsername(),
            book.getBookId(),
            book.getTitle(),
            book.getAuthor(),
            updatedRecord.getBorrowDate(),
            updatedRecord.getDueDate(),
            updatedRecord.getStatus(),
            "Book returned successfully"
        );
        response.setReturnDate(updatedRecord.getReturnDate());
        return response;
    }

    @Override
    public List<BorrowResponse> getUserBorrowHistory(String username) throws UserException {
        
        User user = authService.findUserByUsername(username);
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByUserOrderByBorrowDateDesc(user);
        
        return borrowRecords.stream()
                .map(this::mapToBorrowResponse)
                .collect(Collectors.toList());
    }

    private BorrowResponse mapToBorrowResponse(BorrowRecord borrowRecord) {
        return BorrowResponse.builder()
                .borrowId(borrowRecord.getBorrowId())
                .userId(borrowRecord.getUser().getUserId())
                .username(borrowRecord.getUser().getUsername())
                .bookId(borrowRecord.getBook().getBookId())
                .bookTitle(borrowRecord.getBook().getTitle())
                .bookAuthor(borrowRecord.getBook().getAuthor())
                .borrowDate(borrowRecord.getBorrowDate())
                .dueDate(borrowRecord.getDueDate())
                .returnDate(borrowRecord.getReturnDate())
                .status(borrowRecord.getStatus())
                .success(true)
                .build();
    }
}