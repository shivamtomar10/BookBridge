package com.example.demo.service;

import com.example.demo.model.Customer;

import com.example.demo.model.Book;
import com.example.demo.repository.CustomerRepository;
import java.util.*;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer register(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer login(String email, String password) {
        return customerRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public boolean emailExists(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public Customer getById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    
    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getApprovedBooks() {
        return bookRepository.findByStatus("APPROVED");
    }

    
}
