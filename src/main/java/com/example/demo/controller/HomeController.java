package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    private static final Logger logger = LogManager.getLogger(HomeController.class);

    // ✅ Home Page
    @GetMapping("/")
    public String homePage(Model model) {
        logger.info("Loading Home Page - Fetching approved books");

        List<Book> approvedBooks = bookService.getApprovedBooks();

        logger.info("Approved books fetched: {}", approvedBooks.size());

        model.addAttribute("books", approvedBooks);
        return "generic-dashboard";  // templates/index.html
    }

    // ✅ View Book Details
    @GetMapping("/book/{id}")
    public String viewBook(@PathVariable Long id, Model model) {

        logger.info("Fetching details for bookId: {}", id);

        Book book = bookService.getBookById(id);

        if (book == null) {
            logger.error("Book not found for id: {}", id);
            model.addAttribute("error", "Book not found!");
            return "error-page";
        }

        logger.info("Book details loaded successfully for id: {}", id);

        model.addAttribute("book", book);
        return "book-details";  // templates/book-details.html
    }

    // ✅ Search books by category
    @GetMapping("/search")
    public String searchBooks(@RequestParam("category") String category, Model model) {

        logger.info("Searching books by category: {}", category);

        List<Book> result = bookService.getByCategory(category);

        logger.info("Search results found: {} books in category '{}'", result.size(), category);

        model.addAttribute("books", result);
        model.addAttribute("category", category);

        return "search-results";
    }
}
