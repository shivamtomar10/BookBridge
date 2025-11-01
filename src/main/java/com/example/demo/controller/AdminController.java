package com.example.demo.controller;

import com.example.demo.model.Admin;
import com.example.demo.model.Book;
import com.example.demo.model.PendingBook;
import com.example.demo.model.Review;
import com.example.demo.model.Customer;
import com.example.demo.model.Dealer;

import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.DealerRepository;
import com.example.demo.repository.PendingBookRepository;
import com.example.demo.repository.ReviewRepository;

import com.example.demo.service.AdminService;
import com.example.demo.service.BookService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Autowired private AdminService adminService;
    @Autowired private BookService bookService;

    @Autowired private PendingBookRepository pendingBookRepository;
    @Autowired private BookRepository bookRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private DealerRepository dealerRepo;
    @Autowired private ReviewRepository reviewRepo;

    // ✅ LOGIN PAGE
    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/login")
    public String adminLogin(@RequestParam String username,
                             @RequestParam String password,
                             Model model) {

        Admin admin = adminService.login(username, password);

        if (admin != null) {
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Invalid Credentials!");
            return "admin-login";
        }
    }

    // ✅ DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Book> pending = bookService.getPendingBooks();
        model.addAttribute("pendingCount", pending.size());
        return "admin-dashboard";
    }

    // ✅ PENDING BOOKS
    @GetMapping("/pending-books")
    public String pendingBooks(Model model) {
        model.addAttribute("books", bookService.getPendingBooks());
        return "admin-pending-books";
    }

    @GetMapping("/approve/{id}")
    public String approveBook(@PathVariable Long id) {
        bookService.approveBook(id);
        return "redirect:/admin/pending-books";
    }

    @GetMapping("/reject/{id}")
    public String rejectBook(@PathVariable Long id) {
        bookService.rejectBook(id);
        return "redirect:/admin/pending-books";
    }

    // ✅ DEALER REQUESTS
    @GetMapping("/requests")
    public String dealerRequests(Model model) {
        model.addAttribute("requests", bookService.getPendingBooks());
        return "admin-dealer-requests";
    }

    @GetMapping("/request/approve/{id}")
    public String approveDealerRequest(@PathVariable Long id) {
        bookService.approveBook(id);
        return "redirect:/admin/requests";
    }

    @GetMapping("/request/reject/{id}")
    public String rejectDealerRequest(@PathVariable Long id) {
        bookService.rejectBook(id);
        return "redirect:/admin/requests";
    }

    // ✅ DONATIONS
    @GetMapping("/donations")
    public String viewDonations(Model model) {
        model.addAttribute("pending", pendingBookRepository.findAll());
        return "admin-donations";
    }

    @GetMapping("/donations/approve/{id}")
    public String approveDonation(@PathVariable Long id) {
        bookService.approvePendingBook(id);
        return "redirect:/admin/donations";
    }

    @GetMapping("/donations/reject/{id}")
    public String rejectDonation(@PathVariable Long id) {
        bookService.rejectPendingBook(id);
        return "redirect:/admin/donations";
    }

    // ✅ REPORT DOWNLOAD (ZIP of CSVs)
    @GetMapping("/report")
    public void downloadReport(HttpServletResponse response) throws Exception {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=full-report.zip");

        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

        // ----- BOOKS -----
        zipOut.putNextEntry(new ZipEntry("books.csv"));
        writeBooksCSV(zipOut);
        zipOut.closeEntry();

        // ----- CUSTOMERS -----
        zipOut.putNextEntry(new ZipEntry("customers.csv"));
        writeCustomersCSV(zipOut);
        zipOut.closeEntry();

        // ----- DEALERS -----
        zipOut.putNextEntry(new ZipEntry("dealers.csv"));
        writeDealersCSV(zipOut);
        zipOut.closeEntry();

        // ----- REVIEWS -----
        zipOut.putNextEntry(new ZipEntry("reviews.csv"));
        writeReviewsCSV(zipOut);
        zipOut.closeEntry();

        zipOut.finish();
        zipOut.close();
    }

    // ✅ BOOK CSV
    private void writeBooksCSV(OutputStream out) throws Exception {
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Title,Author,Category,Price,Status");

        for (Book b : bookRepo.findAll()) {
            writer.println(
                    b.getId() + "," + b.getTitle() + "," + b.getAuthor() + "," +
                    b.getCategory() + "," + b.getPrice() + "," + b.getStatus()
            );
        }
        writer.flush();
    }

    // ✅ CUSTOMER CSV
    private void writeCustomersCSV(OutputStream out) throws Exception {
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Name,Email");

        for (Customer c : customerRepo.findAll()) {
            writer.println(c.getId() + "," + c.getName() + "," + c.getEmail());
        }
        writer.flush();
    }

    // ✅ DEALER CSV
    private void writeDealersCSV(OutputStream out) throws Exception {
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Name,Email");

        for (Dealer d : dealerRepo.findAll()) {
            writer.println(d.getId() + "," + d.getName() + "," + d.getEmail());
        }
        writer.flush();
    }

    // ✅ REVIEW CSV
    private void writeReviewsCSV(OutputStream out) throws Exception {
        PrintWriter writer = new PrintWriter(out);
        writer.println("Book,Customer,Rating,Review");

        for (Review r : reviewRepo.findAll()) {
            writer.println(
                    r.getBook().getTitle() + "," +
                    r.getCustomer().getName() + "," +
                    r.getRating() + "," +
                    r.getComment()
            );
        }
        writer.flush();
    }
}
