package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.model.PendingBook;
import com.example.demo.service.BookService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OrdersService;
import com.example.demo.service.ReviewService;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.repository.*;

/**
 * CustomerController - handles registration, login, dashboard and simple customer actions.
 * Kept intentionally simple (no Spring Security) and uses HttpSession for minimal session handling.
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BookService bookService;
    @Autowired
    private PendingBookRepository pendingBookRepository;
    
    @Autowired
    private ReviewService reviewService;

    // -------------------------
    // Registration
    // -------------------------
    @GetMapping("/register")
    public String registerPage(Model model) {
        logger.info("Opening Customer Registration Page");
        // Provide empty object for thymeleaf th:object / th:field binding
        if (!model.containsAttribute("customer")) {
            model.addAttribute("customer", new Customer());
        }
        return "customer-register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Customer customer, Model model) {
        logger.info("New customer registration attempt: {}", customer.getEmail());

        // basic duplicate-email check
        if (customerService.emailExists(customer.getEmail())) {
            logger.warn("Attempt to register with already registered email: {}", customer.getEmail());
            model.addAttribute("error", "Email already registered!");
            model.addAttribute("customer", customer);
            return "customer-register";
        }

        try {
            customerService.register(customer);
            logger.info("Customer registered successfully: {}", customer.getEmail());
            return "redirect:/customer/login";
        } catch (Exception e) {
            logger.error("Error registering customer: {} — {}", customer.getEmail(), e.getMessage(), e);
            model.addAttribute("error", "Registration failed. Please try again.");
            model.addAttribute("customer", customer);
            return "customer-register";
        }
    }

    // -------------------------
    // Login / Logout
    // -------------------------
    @GetMapping("/login")
    public String loginPage() {
        logger.info("Opening Customer Login Page");
        return "customer-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {

        logger.info("Customer trying to log in: {}", email);

        Customer customer = customerService.login(email, password);

        if (customer != null) {
            logger.info("Customer logged in successfully: {}", email);
            session.setAttribute("customer", customer);   // store logged-in user in session
            return "redirect:/customer/dashboard";
        }

        logger.warn("Customer login failed for email: {}", email);
        model.addAttribute("error", "Invalid Credentials!");
        return "customer-login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // -------------------------
    // Dashboard & pages
    // -------------------------
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        logger.info("Customer dashboard opened");
        // show welcome message and optionally other data
        model.addAttribute("message", "Welcome to your Customer Dashboard!");
        // keep session.customer available in thymeleaf via ${session.customer}
        return "customer-dashboard";
    }

    @GetMapping("/books")
    public String browseBooks(Model model, HttpSession session) {

        Customer customer = (Customer) session.getAttribute("customer");

        model.addAttribute("books", bookService.getApprovedBooks());
        model.addAttribute("customerId", customer.getId());

        return "customer-books";
    }

    // Buy page (simple)
    @GetMapping("/buy/{id}")
    public String buyBook(@PathVariable Long id, Model model) {
        logger.info("Customer opened Buy Page for bookId: {}", id);
        model.addAttribute("bookId", id);
        return "buy-page";
    }

    // Donate (GET form)
    @GetMapping("/donate")
    public String donatePage() {
        logger.info("Customer opened Donate Book Page");
        return "donate-page";
    }

    @PostMapping("/donate")
    public String donateBook(@RequestParam String title,
                             @RequestParam String author,
                             @RequestParam(required = false) String description,
                             HttpSession session,
                             Model model) {

        Customer customer = (Customer) session.getAttribute("customer");

        PendingBook pb = new PendingBook();
        pb.setTitle(title);
        pb.setAuthor(author);
        pb.setGenre("Donation");   // or let customer choose later
        pb.setPrice(0.0);          // donations are free
        pb.setDescription(description);
        pb.setCustomerId(customer.getId());
        pb.setStatus("PENDING");

        pendingBookRepository.save(pb);

        model.addAttribute("message", "Thank you! Your donated book is under review.");
        return "customer-dashboard";
    }
    
    @Autowired
    private OrdersService orderService;

    @PostMapping("/buy")
    public String executeBuy(
            @RequestParam Long customerId,
            @RequestParam Long bookId,
            Model model) {

        orderService.buyBook(customerId, bookId);

        model.addAttribute("message", "Purchase successful!");
        return "buy-success";   
    }
    
	
	@GetMapping("/book/{id}")
	public String viewBook(@PathVariable Long id, Model model, HttpSession session) {
	
	    Customer customer = (Customer) session.getAttribute("customer");
	
	    model.addAttribute("book", bookService.getBookById(id));
	    model.addAttribute("reviews", reviewService.getReviewsForBook(id));
	    model.addAttribute("customerId", customer.getId());
	
	    return "book-details";
	}
	
	@PostMapping("/review")
	public String postReview(@RequestParam Long customerId,
	                         @RequestParam Long bookId,
	                         @RequestParam int rating,
	                         @RequestParam String comment,
	                         RedirectAttributes redirectAttributes) {
	
	    boolean ok = reviewService.addReview(customerId, bookId, rating, comment);
	
	    if (!ok)
	        redirectAttributes.addFlashAttribute("error", "You have already reviewed this book.");
	    else
	        redirectAttributes.addFlashAttribute("msg", "Review submitted successfully!");
	
	    return "redirect:/customer/book/" + bookId;
	}



    
   

    
}
