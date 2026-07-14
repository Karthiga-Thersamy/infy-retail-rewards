package com.retail.rewards.repository;

import com.retail.rewards.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>  {
}
