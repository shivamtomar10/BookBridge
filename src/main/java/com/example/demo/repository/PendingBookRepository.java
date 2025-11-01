package com.example.demo.repository;

import com.example.demo.model.PendingBook;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*
;public interface PendingBookRepository extends JpaRepository<PendingBook, Long> {

	
	List<PendingBook> findByDealerId(Long dealerId);
}

