package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Customer;
import com.example.demo.model.OrderRecord;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrdersService {

    @Autowired
    private OrderRecordRepository orderRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private BookRepository bookRepo;

    public void buyBook(Long customerId, Long bookId) {

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        OrderRecord order = new OrderRecord();
        order.setCustomer(customer);
        order.setBook(book);
        order.setOrderDate(LocalDate.now());

        orderRepo.save(order);
    }
}
