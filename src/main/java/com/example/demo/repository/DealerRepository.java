package com.example.demo.repository;

import com.example.demo.model.*;
import java.util.*;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

    // ✅ For dealer login
    Dealer findByEmailAndPassword(String email, String password);

    // ✅ Check duplicate dealer
    boolean existsByEmail(String email);
    
    @Query("SELECT b FROM Book b WHERE b.dealer.id = :dealerId")
    List<Book> findBooksByDealerId(@Param("dealerId") Long dealerId);
    
}
