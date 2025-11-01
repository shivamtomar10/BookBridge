package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.CartItem;
import com.example.demo.model.OrderHistory;
import com.example.demo.repository.*;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CartItemRepository cartRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private OrderHistoryRepository orderRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerRepository customerRepository;

    
    public boolean placeOrder(Long customerId) {

        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return false;

        List<CartItem> items = cartRepo.findByCustomer(customer);
        if (items == null || items.isEmpty()) return false;

        // ✅ No stock checking
        // ✅ No quantity deduction from book

        for (CartItem item : items) {

            OrderHistory order = new OrderHistory();
            order.setCustomer(customer);
            order.setBookTitle(item.getBook().getTitle());
            order.setQuantity(item.getQuantity());
            order.setOrderDate(new java.util.Date());

            orderRepo.save(order);
        }

        // ✅ Empty cart after placing order
        cartService.clearCart(customerId);

        return true;
    }
    

}
