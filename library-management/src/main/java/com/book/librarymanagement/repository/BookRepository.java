package com.book.librarymanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.book.librarymanagement.entity.Books;

@Repository
public interface BookRepository extends JpaRepository<Books, UUID> {
    
    List<Books> findByTitleContainingIgnoreCase(String title);
    
    List<Books> findByAuthorContainingIgnoreCase(String author);
    
    @Query("SELECT b FROM Books b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Books> findByTitleOrAuthorContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    List<Books> findByAvailableCopiesGreaterThan(Integer minCopies);
    
    boolean existsByIsbn(String isbn);
}