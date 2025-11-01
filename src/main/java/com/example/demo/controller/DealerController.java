package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Dealer;
import com.example.demo.model.PendingBook;
import com.example.demo.service.BookService;
import com.example.demo.service.DealerService;
import com.example.demo.service.PendingBookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
@RequestMapping("/dealer")
public class DealerController {

    @Autowired
    private DealerService dealerService;
    
    @Autowired
    private PendingBookService pendingBookService;

    @Autowired
    private BookService bookService;

    private static final Logger logger = LogManager.getLogger(DealerController.class);

    // ✅ Dealer login page
    @GetMapping("/login")
    public String dealerLoginPage() {
        logger.info("Opening Dealer Login Page");
        return "dealer-login";
    }

    // ✅ Dealer login processing
    @PostMapping("/login")
    public String dealerLogin(@RequestParam String email,
                              @RequestParam String password,
                              Model model) {

        logger.info("Dealer attempting login: {}", email);

        Dealer dealer = dealerService.login(email, password);

        if (dealer != null) {
            logger.info("Dealer logged in successfully: {}", email);
            return "redirect:/dealer/dashboard?id=" + dealer.getId();
        }

        logger.warn("Dealer login failed for email: {}", email);
        model.addAttribute("error", "Invalid Credentials!");

        return "dealer-login";
    }

 // ✅ Registration Page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("dealer", new Dealer()); // ✅ important
        return "dealer-register";
    }

    // ✅ Registration Processing
    @PostMapping("/register")
    public String registerDealer(@ModelAttribute Dealer dealer, Model model) {

        if (dealerService.emailExists(dealer.getEmail())) {
            model.addAttribute("error", "Email already registered!");
            model.addAttribute("dealer", dealer);
            return "dealer-register";
        }

        dealerService.register(dealer);
        return "redirect:/dealer/login";
    }
    @PostMapping("/upload-books")
    public String uploadBooks(@RequestParam("file") MultipartFile file,
                              @RequestParam("dealerId") Long dealerId,
                              RedirectAttributes redirectAttributes) {
        try {
            bookService.processUploadedFile(file, dealerId);
            redirectAttributes.addFlashAttribute("msg", "Books uploaded for admin approval!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }

        return "redirect:/dealer/dashboard?id=" + dealerId;
    }

   
    
    
    // ✅ Dealer Dashboard
    @GetMapping("/dashboard")
    public String dealerDashboard(@RequestParam Long id, Model model) {

        logger.info("Dealer opening dashboard. DealerId: {}", id);

        Dealer dealer = dealerService.getDealerById(id);
        List<Book> books = bookService.getBooksByDealer(id);

        if (dealer == null) {
            logger.error("Dealer not found for id: {}", id);
            model.addAttribute("error", "Dealer not found!");
            return "error-page";
        }

        model.addAttribute("dealer", dealer);
        model.addAttribute("books", books);

        return "dealer-dashboard";
    }

    // ✅ Upload Book Form
    @GetMapping("/upload-book")
    public String uploadBookForm(@RequestParam Long dealerId, Model model) {
        model.addAttribute("dealerId", dealerId);
        return "dealer-upload-book";
    }

    // ✅ Upload Book Request
    @PostMapping("/upload-book")
    public String uploadBook(@ModelAttribute Book book, @RequestParam Long dealerId) {

        logger.info("Dealer (ID: {}) uploading new book: {}", dealerId, book.getTitle());

        dealerService.addBook(book, dealerId);

        return "redirect:/dealer/dashboard?id=" + dealerId;
    }
    
    @GetMapping("/donate-book")
    public String donateBookForm(@RequestParam Long dealerId, Model model) {
        model.addAttribute("dealerId", dealerId);
        return "dealer-donate-book"; // ✅ use new HTML file
    }
    
    
    
    
    @PostMapping("/donate-book")
    public String donateBook(@ModelAttribute PendingBook pendingBook,
                             @RequestParam Long dealerId,
                             RedirectAttributes redirectAttributes) {

        logger.info("Dealer (ID: {}) donated new book -> Pending Approval: {}", dealerId, pendingBook.getTitle());

        pendingBookService.savePendingBook(pendingBook, dealerId);

        redirectAttributes.addFlashAttribute("msg",
                "Your book has been submitted for admin approval!");

        return "redirect:/dealer/dashboard?id=" + dealerId;
    }


    
}
