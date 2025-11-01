package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
 

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long customerId,
                            @RequestParam Long bookId) {

        cartService.addToCart(customerId, bookId);
        return "redirect:/customer/books?customerId=" + customerId;  
    }


    @GetMapping
    public String viewCart(@RequestParam Long customerId, Model model) {
        model.addAttribute("items", cartService.getCustomerCart(customerId));
        model.addAttribute("customerId", customerId);
        return "customer-cart";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam Long id,
                         @RequestParam Long customerId) {

        cartService.removeItem(id);
        return "redirect:/cart?customerId=" + customerId;
    }

    @PostMapping("/buy")
    public String buy(@RequestParam Long customerId,
                      RedirectAttributes redirectAttributes) {

        boolean success = orderService.placeOrder(customerId);

        if (!success)
            redirectAttributes.addFlashAttribute("error", "Cart is empty!");
        else
            redirectAttributes.addFlashAttribute("msg", "Purchase Successful!");

        return "redirect:/customer/books?customerId=" + customerId;

    }
}
