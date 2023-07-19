package com.clothifystore.controller;

import com.clothifystore.entity.Order;
import com.clothifystore.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepo orderRepo;

    @PostMapping
    public ResponseEntity<Order> addOrder(@RequestBody Order order){
        return ResponseEntity.ok(orderRepo.save(order));
    }
}
