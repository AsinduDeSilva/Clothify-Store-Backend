package com.clothifystore.repository;

import com.clothifystore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustomerRepo extends JpaRepository<Customer,Integer> {
    Customer findByCustomerID(int customerID);
}
