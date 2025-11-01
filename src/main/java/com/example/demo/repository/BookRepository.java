package com.example.demo.repository;

import com.example.demo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ✅ Search by category
    List<Book> findByCategory(String category);

    // ✅ Search by title (contains)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // ✅ Dealer submitted books (pending approval)
    List<Book> findByDealer_Id(Long dealerId);

    // ✅ Admin sees only approved books
    List<Book> findByStatus(String status);

}
