package com.clothifystore.controller;

import com.clothifystore.dto.request.OrderRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.dto.response.OrderResponseDTO;
import com.clothifystore.dto.response.OrderStatsResponseDTO;
import com.clothifystore.dto.response.WeekOrderDataResponseDTO;
import com.clothifystore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CrudResponse> addOrder(@RequestBody OrderRequestDTO request)
            throws MessagingException, UnsupportedEncodingException {
        orderService.addOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CrudResponse(true, "Order placed"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable(value = "id") int orderID) {
        return ResponseEntity.ok(orderService.getOrder(orderID));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByStatus(
            @PathVariable(value = "status") int status,
            @RequestParam(value = "page") int page) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status, page));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersOf(
            @PathVariable(value = "id") int customerID,
            @RequestParam(value = "page") int page) {
        return ResponseEntity.ok(orderService.getOrdersOfCustomer(customerID, page));
    }

    @GetMapping("/customer/count/{id}")
    public ResponseEntity<Integer> getOngoingOrderCountOf(@PathVariable(value = "id") int customerID) {
        return ResponseEntity.ok(orderService.getOngoingOrderCountOfCustomer(customerID));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateOrderStatus(
            @PathVariable(value = "id") int orderID,
            @RequestParam(value = "status") int status)
            throws MessagingException, UnsupportedEncodingException {
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
