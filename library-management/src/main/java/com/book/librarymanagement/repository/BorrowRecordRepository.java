package com.book.librarymanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.book.librarymanagement.entity.BorrowRecord;
import com.book.librarymanagement.entity.User;
import com.book.librarymanagement.enums.BorrowStatus;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {
    
    List<BorrowRecord> findByUserOrderByBorrowDateDesc(User user);
    
    List<BorrowRecord> findByUserAndStatus(User user, BorrowStatus status);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.user = :user AND br.book.bookId = :bookId AND br.status = :status")
    List<BorrowRecord> findByUserAndBookAndStatus(@Param("user") User user, 
                                                 @Param("bookId") UUID bookId, 
                                                 @Param("status") BorrowStatus status);
    
    boolean existsByUserAndBookBookIdAndStatus(User user, UUID bookId, BorrowStatus status);
}