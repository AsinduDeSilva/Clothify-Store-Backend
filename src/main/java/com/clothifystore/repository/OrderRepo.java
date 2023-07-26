package com.clothifystore.repository;

import com.clothifystore.entity.Order;
import com.clothifystore.enums.OrderStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer> {

    List<Order> findByStatus(OrderStatusTypes status);
    List<Order> findByCustomerID(int customerID);

}
