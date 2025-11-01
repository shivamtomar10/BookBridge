package com.example.demo.service;

import com.example.demo.model.Dealer;
import com.example.demo.model.Book; 
import java.util.*
;public interface DealerService {

	Dealer register(Dealer dealer);

    Dealer login(String email, String password);

    boolean emailExists(String email);

    Dealer getDealerById(Long id); 
    
    Dealer addBook(Book book, Long dealerId);
    
    List<Book> getBooksByDealer(Long dealerId);

    
}
