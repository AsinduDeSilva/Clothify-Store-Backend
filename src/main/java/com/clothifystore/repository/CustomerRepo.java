package com.clothifystore.repository;

import com.clothifystore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepo extends JpaRepository<Customer,Integer> {
    int countAllByEmail(String email);

}
