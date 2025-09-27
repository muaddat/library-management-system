# Library Management System API

A comprehensive RESTful API for managing a digital library system, enabling user registration, book management, and borrowing operations with JWT-based authentication.

## 📋 Table of Contents

- [Business Scenario](#business-scenario)
- [Features](#features)
- [Technical Stack](#technical-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Testing](#testing)
- [Configuration](#configuration)

## 🎯 Business Scenario

A small library wants to digitize its system. They need a RESTful API where users can register, borrow books, and track their borrow history. The API should also allow an admin to manage the books available in the library.

## ✨ Features

### User Management
- ✅ User registration with role-based access (USER/ADMIN)
- ✅ Secure login with JWT authentication
- ✅ Password encryption using BCrypt
- ✅ Username and email uniqueness validation

### Book Management (Admin Only)
- ✅ Add new books to the library
- ✅ Update existing book details
- ✅ Remove books from the library
- ✅ ISBN uniqueness validation

### Book Discovery (All Users)
- ✅ View all books in the library
- ✅ Search books by title or author
- ✅ Check book availability status
- ✅ View total and available copies

### Borrowing & Returning
- ✅ Borrow available books (14-day loan period)
- ✅ Return borrowed books
- ✅ View personal borrowing history
- ✅ Prevent duplicate borrowing of same book
- ✅ Automatic inventory management

## 🛠 Technical Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Database**: H2 (In-memory)
- **ORM**: Spring Data JPA
- **Security**: Spring Security with JWT
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Documentation**: Lombok for boilerplate code
- **Validation**: Jakarta Bean Validation

## 📁 Project Structure

```
library-management/
├── src/
│   ├── main/
│   │   ├── java/com/book/librarymanagement/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BookController.java
│   │   │   │   └── BorrowController.java
│   │   │   ├── dto/
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── BookRequest.java
│   │   │   │   ├── BookResponse.java
│   │   │   │   ├── BorrowRequest.java
│   │   │   │   ├── BorrowResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── SignUpRequest.java
│   │   │   ├── entity/
│   │   │   │   ├── Books.java
│   │   │   │   ├── BorrowRecord.java
│   │   │   │   └── User.java
│   │   │   ├── enums/
│   │   │   │   ├── BorrowStatus.java
│   │   │   │   └── Role.java
│   │   │   ├── exception/
│   │   │   │   ├── ErrorDetails.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InsufficientPermissionException.java
│   │   │   │   └── UserException.java
│   │   │   ├── repository/
│   │   │   │   ├── BookRepository.java
│   │   │   │   ├── BorrowRecordRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtConstant.java
│   │   │   │   ├── JwtProvider.java
│   │   │   │   └── JwtValidator.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── BookService.java
│   │   │   │   └── BorrowService.java
│   │   │   └── service/impl/
│   │   │       ├── AuthServiceImpl.java
│   │   │       ├── BookServiceImpl.java
│   │   │       ├── BorrowServiceImpl.java
│   │   │       └── CustomUserDetailsService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/book/librarymanagement/
│           └── service/impl/
│               └── BookServiceImplTest.java
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd library-management
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - H2 Database Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:librarydb`
     - Username: `libraryadmin`
     - Password: `password`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "username": "johndoe",
  "email": "john@example.com",
  "role": "USER",
  "issuedAt": "2024-01-01T10:00:00",
  "message": "User registered successfully",
  "success": true
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

### Book Management Endpoints

#### Get All Books (with optional search)
```http
GET /api/books
Authorization: Bearer <token>
```

#### Create Book (Admin Only)
```http
POST /api/books
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "description": "A handbook of agile software craftsmanship",
  "totalCopies": 5,
  "availableCopies": 5
}
```

#### Update Book (Admin Only)
```http
PUT /api/books/{bookId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Clean Code - Updated",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "description": "Updated description",
  "totalCopies": 10,
  "availableCopies": 8
}
```

#### Delete Book (Admin Only)
```http
DELETE /api/books/{bookId}
Authorization: Bearer <token>
```

### Borrowing Endpoints

#### Borrow Book
```http
POST /api/borrow
Authorization: Bearer <token>
Content-Type: application/json

{
  "bookId": "123e4567-e89b-12d3-a456-426614174000"
}
```

#### Return Book
```http
POST /api/return?bookId={bookId}
Authorization: Bearer <token>
```

#### Get Borrow History
```http
GET /api/history
Authorization: Bearer <token>
```

### HTTP Status Codes

- `200 OK` - Successful GET/PUT requests
- `201 Created` - Successful POST requests
- `204 No Content` - Successful DELETE requests
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## 🗄 Database Schema

### Users Table
```sql
CREATE TABLE USERS (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Books Table
```sql
CREATE TABLE BOOKS (
    book_id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) UNIQUE,
    description TEXT,
    total_copies INTEGER NOT NULL,
    available_copies INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Borrow Records Table
```sql
CREATE TABLE BORROW_RECORDS (
    borrow_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    book_id UUID NOT NULL,
    borrow_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    FOREIGN KEY (book_id) REFERENCES BOOKS(book_id)
);
```

## 🔒 Security

### Authentication & Authorization
- **JWT-based authentication** with secure token generation
- **Role-based access control** (USER, ADMIN)
- **Password encryption** using BCrypt
- **Method-level security** with `@PreAuthorize` annotations

### Security Configuration
- **CORS enabled** for cross-origin requests
- **CSRF disabled** for stateless API
- **Session management** set to stateless
- **H2 console** enabled for development

### JWT Token Structure
```json
{
  "iss": "library-management-api",
  "exp": 1640995200,
  "email": "user@example.com",
  "authorities": "ROLE_USER"
}
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BookServiceImplTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Categories
- **Unit Tests**: Service layer testing with Mockito
- **Integration Tests**: Controller and repository testing
- **Security Tests**: Authentication and authorization testing

### Sample Test Coverage
- ✅ BookServiceImpl - Complete CRUD operations testing
- ✅ Authentication flow testing
- ✅ Validation testing
- ✅ Exception handling testing

## ⚙️ Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:librarydb
spring.datasource.username=libraryadmin
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### JWT Configuration
- **Secret Key**: Configurable in `JwtConstant.java`
- **Token Expiration**: 24 hours (86400000 ms)
- **Header Name**: `Authorization`
- **Token Prefix**: `Bearer `

## 🚦 Error Handling

### Global Exception Handler
- **UserException**: Business logic errors (400 Bad Request)
- **ValidationException**: Input validation errors (400 Bad Request)
- **AuthenticationException**: Authentication errors (401 Unauthorized)
- **AccessDeniedException**: Authorization errors (403 Forbidden)
- **General Exception**: Server errors (500 Internal Server Error)

### Error Response Format
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "message": "Error description",
  "details": "uri=/api/books",
  "status": 400
}
```

## 🎯 Business Rules

### User Management
- Usernames must be unique and 3-20 characters
- Emails must be valid and unique
- Passwords must be at least 6 characters
- Default role is USER

### Book Management
- ISBNs must be unique when provided
- Available copies cannot exceed total copies
- Only ADMINs can create/update/delete books
- Books with zero available copies are marked unavailable

### Borrowing Rules
- Users cannot borrow the same book twice simultaneously
- Loan period is 14 days
- Books are automatically marked unavailable when all copies are borrowed
- Returned books increment available copies

