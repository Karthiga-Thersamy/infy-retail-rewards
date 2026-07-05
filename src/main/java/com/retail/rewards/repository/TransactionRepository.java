package com.retail.rewards.repository;

import com.retail.rewards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerIdAndDateBetween(Long customerId, LocalDate start, LocalDate end);
}