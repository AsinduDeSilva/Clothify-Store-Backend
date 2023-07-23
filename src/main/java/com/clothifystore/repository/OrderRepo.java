package com.clothifystore.repository;

import com.clothifystore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order,Integer> {

    List<Order> findByStatus(String status);
    List<Order> findByCustomerID(int customerID);

}
