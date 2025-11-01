package com.example.demo.service;

import com.example.demo.model.Dealer;
import com.example.demo.model.Book;
import com.example.demo.repository.DealerRepository;
import com.example.demo.repository.BookRepository;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServiceImpl implements DealerService {

    @Autowired
    private DealerRepository dealerRepository;
    
    @Autowired
    private BookRepository bookRepository;

    @Override
    public Dealer register(Dealer dealer) {
        return dealerRepository.save(dealer);
    }

    @Override
    public Dealer login(String email, String password) {
        return dealerRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public boolean emailExists(String email) {
        return dealerRepository.existsByEmail(email);
    }
    
    @Override
    public Dealer getDealerById(Long id) {
        return dealerRepository.findById(id).orElse(null);
    }

    //Add Book by Dealer
    @Override
    public Dealer addBook(Book book, Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId).orElse(null);

        if (dealer != null) {
            book.setDealer(dealer);
            book.setStatus("PENDING");  // must be approved by admin
            bookRepository.save(book);
        }
        return dealer;
    }
    @Override
    public List<Book> getBooksByDealer(Long dealerId) {
        return dealerRepository.findBooksByDealerId(dealerId);
    }
    
}
