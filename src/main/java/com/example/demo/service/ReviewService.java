package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Customer;
import com.example.demo.model.Review;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private BookRepository bookRepo;

    public List<Review> getReviewsForBook(Long bookId) {
        return reviewRepo.findByBookId(bookId);
    }

    public boolean addReview(Long customerId, Long bookId, int rating, String comment) {

        // ✅ prevent duplicate review
        if (reviewRepo.existsByCustomerIdAndBookId(customerId, bookId)) {
            return false;
        }

        Customer customer = customerRepo.findById(customerId).orElse(null);
        Book book = bookRepo.findById(bookId).orElse(null);

        Review review = new Review();
        review.setCustomer(customer);
        review.setBook(book);
        review.setRating(rating);
        review.setComment(comment);
        review.setDate(LocalDate.now());

        reviewRepo.save(review);
        return true;
    }
}
