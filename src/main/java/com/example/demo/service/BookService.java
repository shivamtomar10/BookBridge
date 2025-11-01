package com.example.demo.service;

import com.example.demo.model.Book;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    Book addBook(Book book);
    Book updateBook(Book book);
    void deleteBook(Long id);
    Book getBookById(Long id);

    List<Book> getAllBooks();
    List<Book> getByCategory(String category);
    List<Book> searchByTitle(String title);

    List<Book> getBooksByDealer(Long dealerId);

    // For admin
    List<Book> getApprovedBooks();
    List<Book> getPendingBooks();
    void approveBook(Long id);
    void rejectBook(Long id);
    void processUploadedFile(MultipartFile file, Long dealerId) throws Exception;
    void approvePendingBook(Long pendingId);
    void rejectPendingBook(Long pendingId);

    public List<Book> getPendingDonationBooks();

    

}
