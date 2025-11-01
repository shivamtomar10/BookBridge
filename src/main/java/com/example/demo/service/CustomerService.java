package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Book;
import java.util.*;

public interface CustomerService {

    Customer register(Customer customer);

    Customer login(String email, String password);

    boolean emailExists(String email);
    
    List<Book> getApprovedBooks();

    Customer getById(Long id);
}
