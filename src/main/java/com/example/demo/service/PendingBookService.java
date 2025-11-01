package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Dealer;
import com.example.demo.model.PendingBook;
import com.example.demo.repository.DealerRepository;
import com.example.demo.repository.PendingBookRepository;

@Service
public class PendingBookService {

    @Autowired
    private PendingBookRepository pendingRepo;

    @Autowired
    private DealerRepository dealerRepo;

    public void savePendingBook(PendingBook pendingBook, Long dealerId) {

        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Dealer not found"));

        pendingBook.setDealerId(dealer.getId());
        pendingBook.setStatus("PENDING");

        pendingRepo.save(pendingBook);
    }
}

