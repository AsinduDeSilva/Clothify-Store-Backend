package com.clothifystore.repository;

import com.clothifystore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order,Integer> {
}
