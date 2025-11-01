package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ✅ For customer login
    Customer findByEmailAndPassword(String email, String password);

    // ✅ To avoid duplicate registrations
    boolean existsByEmail(String email);
}
