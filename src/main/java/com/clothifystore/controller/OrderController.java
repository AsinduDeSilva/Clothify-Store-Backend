package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.dto.response.OrderStatsResponseDTO;
import com.clothifystore.dto.response.WeekOrderDataResponseDTO;
import com.clothifystore.entity.Order;
import com.clothifystore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<CrudResponse> addOrder(@RequestBody Order order) throws MessagingException, UnsupportedEncodingException {
        orderService.addOrder(order);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CrudResponse(true, "Order placed"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable(value = "id") int orderID) {
        return ResponseEntity.ok(orderService.getOrder(orderID));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable(value = "status") int status,
                                               @RequestParam(value = "page") int page) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status, page));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Page<Order>> getOrdersOf(@PathVariable(value = "id") int customerID,
                                                   @RequestParam(value = "page") int page) {
        return ResponseEntity.ok(orderService.getOrdersOfCustomer(customerID, page));
    }

    @GetMapping("/customer/count/{id}")
    public ResponseEntity<Integer> getOngoingOrderCountOf(@PathVariable(value = "id") int customerID) {
        return ResponseEntity.ok(orderService.getOngoingOrderCountOfCustomer(customerID));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateOrderStatus(@PathVariable(value = "id") int orderID,
                                                          @RequestParam(value = "status") int status) throws MessagingException, UnsupportedEncodingException {
        orderService.updateOrderStatus(orderID, status);
        return ResponseEntity.ok(new CrudResponse(true, "Order Updated"));
    }

    @GetMapping("week")
    public ResponseEntity<WeekOrderDataResponseDTO> getTotalOrdersCountOfPast7Days() {
        return ResponseEntity.ok(orderService.getTotalOrdersCountOfPast7Days());
    }

    @GetMapping("stats")
    public ResponseEntity<OrderStatsResponseDTO> getSalesIncome() {
        return ResponseEntity.ok(orderService.getSalesIncome());
    }
}
