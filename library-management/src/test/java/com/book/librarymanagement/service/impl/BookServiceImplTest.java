package com.book.librarymanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.book.librarymanagement.dto.BookRequest;
import com.book.librarymanagement.dto.BookResponse;
import com.book.librarymanagement.entity.Books;
import com.book.librarymanagement.exception.UserException;
import com.book.librarymanagement.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Books testBook;
    private BookRequest testBookRequest;
    private UUID testBookId;

    @BeforeEach
    void setUp() {
        testBookId = UUID.randomUUID();
        
        testBook = new Books();
        testBook.setBookId(testBookId);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("978-0123456789");
        testBook.setDescription("A test book");
        testBook.setTotalCopies(5);
        testBook.setAvailableCopies(3);
        testBook.setCreatedAt(LocalDateTime.now());
        testBook.setUpdatedAt(LocalDateTime.now());

        testBookRequest = new BookRequest();
        testBookRequest.setTitle("Test Book");
        testBookRequest.setAuthor("Test Author");
        testBookRequest.setIsbn("978-0123456789");
        testBookRequest.setDescription("A test book");
        testBookRequest.setTotalCopies(5);
        testBookRequest.setAvailableCopies(3);
    }

    @Test
    void createBook_Success() throws UserException {
        // Given
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Books.class))).thenReturn(testBook);

        // When
        BookResponse result = bookService.createBook(testBookRequest);

        // Then
        assertNotNull(result);
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals(testBook.getAuthor(), result.getAuthor());
        assertEquals(testBook.getIsbn(), result.getIsbn());
        assertEquals(testBook.getTotalCopies(), result.getTotalCopies());
        assertEquals(testBook.getAvailableCopies(), result.getAvailableCopies());
        assertTrue(result.isAvailable());
        
        verify(bookRepository, times(1)).existsByIsbn(testBookRequest.getIsbn());
        verify(bookRepository, times(1)).save(any(Books.class));
    }

    @Test
    void createBook_DuplicateISBN_ThrowsException() {
        // Given
        when(bookRepository.existsByIsbn(testBookRequest.getIsbn())).thenReturn(true);

        // When & Then
        UserException exception = assertThrows(UserException.class, 
            () -> bookService.createBook(testBookRequest));
        
        assertEquals("A book with this ISBN already exists", exception.getMessage());
        verify(bookRepository, times(1)).existsByIsbn(testBookRequest.getIsbn());
        verify(bookRepository, never()).save(any(Books.class));
    }

    @Test
    void createBook_AvailableCopiesExceedTotal_ThrowsException() {
        // Given
        testBookRequest.setTotalCopies(3);
        testBookRequest.setAvailableCopies(5);

        // When & Then
        UserException exception = assertThrows(UserException.class, 
            () -> bookService.createBook(testBookRequest));
        
        assertEquals("Available copies cannot exceed total copies", exception.getMessage());
        verify(bookRepository, never()).existsByIsbn(anyString());
        verify(bookRepository, never()).save(any(Books.class));
    }

    @Test
    void updateBook_Success() throws UserException {
        // Given
        UUID bookId = testBook.getBookId();
        BookRequest updateRequest = new BookRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setAuthor("Updated Author");
        updateRequest.setIsbn("978-0987654321");
        updateRequest.setDescription("Updated description");
        updateRequest.setTotalCopies(10);
        updateRequest.setAvailableCopies(8);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByIsbn(updateRequest.getIsbn())).thenReturn(false);
        
        Books updatedBook = new Books();
        updatedBook.setBookId(bookId);
        updatedBook.setTitle(updateRequest.getTitle());
        updatedBook.setAuthor(updateRequest.getAuthor());
        updatedBook.setIsbn(updateRequest.getIsbn());
        updatedBook.setDescription(updateRequest.getDescription());
        updatedBook.setTotalCopies(updateRequest.getTotalCopies());
        updatedBook.setAvailableCopies(updateRequest.getAvailableCopies());
        updatedBook.setCreatedAt(testBook.getCreatedAt());
        updatedBook.setUpdatedAt(LocalDateTime.now());
        
        when(bookRepository.save(any(Books.class))).thenReturn(updatedBook);

        // When
        BookResponse result = bookService.updateBook(bookId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getTitle(), result.getTitle());
        assertEquals(updateRequest.getAuthor(), result.getAuthor());
        assertEquals(updateRequest.getIsbn(), result.getIsbn());
        assertEquals(updateRequest.getTotalCopies(), result.getTotalCopies());
        assertEquals(updateRequest.getAvailableCopies(), result.getAvailableCopies());
        
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).existsByIsbn(updateRequest.getIsbn());
        verify(bookRepository, times(1)).save(any(Books.class));
    }

    @Test
    void updateBook_BookNotFound_ThrowsException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        UserException exception = assertThrows(UserException.class, 
            () -> bookService.updateBook(nonExistentId, testBookRequest));
        
        assertEquals("Book not found with ID: " + nonExistentId, exception.getMessage());
        verify(bookRepository, times(1)).findById(nonExistentId);
        verify(bookRepository, never()).save(any(Books.class));
    }

    @Test
    void getAllBooks_Success() {
        // Given
        Books book2 = new Books();
        book2.setBookId(UUID.randomUUID());
        book2.setTitle("Another Book");
        book2.setAuthor("Another Author");
        book2.setTotalCopies(3);
        book2.setAvailableCopies(2);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());

        List<Books> books = Arrays.asList(testBook, book2);
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<BookResponse> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testBook.getTitle(), result.get(0).getTitle());
        assertEquals(book2.getTitle(), result.get(1).getTitle());
        
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void searchBooks_Success() {
        // Given
        String searchTerm = "test";
        List<Books> books = Arrays.asList(testBook);
        when(bookRepository.findByTitleOrAuthorContainingIgnoreCase(searchTerm)).thenReturn(books);

        // When
        List<BookResponse> result = bookService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getTitle(), result.get(0).getTitle());
        
        verify(bookRepository, times(1)).findByTitleOrAuthorContainingIgnoreCase(searchTerm);
    }

    @Test
    void deleteBook_Success() throws UserException {
        // Given
        when(bookRepository.findById(testBookId)).thenReturn(Optional.of(testBook));

        // When
        bookService.deleteBook(testBookId);

        // Then
        verify(bookRepository, times(1)).findById(testBookId);
        verify(bookRepository, times(1)).delete(testBook);
    }

    @Test
    void deleteBook_BookNotFound_ThrowsException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        UserException exception = assertThrows(UserException.class, 
            () -> bookService.deleteBook(nonExistentId));
        
        assertEquals("Book not found with ID: " + nonExistentId, exception.getMessage());
        verify(bookRepository, times(1)).findById(nonExistentId);
        verify(bookRepository, never()).delete(any(Books.class));
    }
}