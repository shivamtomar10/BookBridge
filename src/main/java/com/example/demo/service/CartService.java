package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.CartItem;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CustomerRepository;
import java.util.*;


@Service
public class CartService {

    @Autowired
    private CartItemRepository cartRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private CustomerRepository customerRepo;

    // ✅ Add book to cart
    public void addToCart(Long customerId, Long bookId) {

        var customer = customerRepo.findById(customerId).orElse(null);
        var book = bookRepo.findById(bookId).orElse(null);

        CartItem item = cartRepo.findByCustomerAndBook(customer, book);

        if (item != null) {
            item.setQuantity(item.getQuantity() + 1);
            cartRepo.save(item);
        } else {
            CartItem newItem = new CartItem(customer, book, 1);
            cartRepo.save(newItem);
        }
    }

    public List<CartItem> getCustomerCart(Long customerId) {
        var customer = customerRepo.findById(customerId).orElse(null);
        return cartRepo.findByCustomer(customer);
    }

    public void removeItem(Long id) {
        cartRepo.deleteById(id);
    }

    public void clearCart(Long customerId) {
        var customer = customerRepo.findById(customerId).orElse(null);
        cartRepo.deleteAll(cartRepo.findByCustomer(customer));
    }
}
