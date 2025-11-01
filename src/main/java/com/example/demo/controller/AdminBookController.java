package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.PendingBook;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.PendingBookRepository;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
@RequestMapping("/admin/books")
public class AdminBookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private PendingBookRepository pendingBookRepository;


    // ✅ 1. List all books
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getApprovedBooks()); 
        return "admin-book-list";
    }

    // ✅ 2. Show add book form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin-add-book";
    }

    // ✅ 3. Handle add book form
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) {
        book.setStatus("APPROVED");  // Admin books are auto-approved
        bookService.addBook(book);
        return "redirect:/admin/books";
    }

    // ✅ 4. Edit book form
    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "admin-edit-book";
    }

    // ✅ 5. Update book
    @PostMapping("/update")
    public String updateBook(@ModelAttribute Book book) {
        bookService.updateBook(book);
        return "redirect:/admin/books";
    }

    // ✅ 6. Delete book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/books";
    }

    // ✅ 7. Approve (optional)
    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        bookService.approveBook(id);
        return "redirect:/admin/books";
    }

    // ✅ 8. Reject (optional)
    @GetMapping("/reject/{id}")
    public String reject(@PathVariable Long id) {
        bookService.rejectBook(id);
        return "redirect:/admin/books";
    }
    



    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/pending")
    public String pendingBooks(Model model) {
        model.addAttribute("pendingBooks", pendingBookRepository.findAll());
        return "admin-pending-books";
    }
    
    @PostMapping("/approve-pending/{id}")
    public String approvePending(@PathVariable Long id) {

        PendingBook pb = pendingBookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        Book book = new Book();
        book.setTitle(pb.getTitle());
        book.setAuthor(pb.getAuthor());
//        book.setGenre(pb.getGenre());
        book.setPrice(pb.getPrice());
        book.setStatus("AVAILABLE");
//        book.setDealerId(pb.getDealerId());

        bookRepository.save(book);

        pendingBookRepository.delete(pb);

        return "redirect:/admin/books/pending";
    }


    

    
}
