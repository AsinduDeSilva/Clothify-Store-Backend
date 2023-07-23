package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Order;
import com.clothifystore.entity.OrderDetail;
import com.clothifystore.entity.Product;
import com.clothifystore.enums.OrderStatusTypes;
import com.clothifystore.repository.OrderRepo;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @PostMapping
    public ResponseEntity<CrudResponse> addOrder(@RequestBody Order order){

        for (OrderDetail orderDetail : order.getOrderDetails()){
            if(productRepo.findById(orderDetail.getProductID()).isPresent()){
                Product product = productRepo.findById(orderDetail.getProductID()).get();
                int restQty;
                switch (orderDetail.getSize()){
                    case "S" :
                        restQty = product.getSmallQty()-orderDetail.getQuantity();
                        if(restQty<0){
                            return ResponseEntity.badRequest().body(
                                    new CrudResponse(false,"Do not have enough stock")
                            );
                        }else{
                            product.setSmallQty(restQty);
                        }
                        break;
                    case "M" :
                        restQty = product.getMediumQty()-orderDetail.getQuantity();
                        if(restQty<0){
                            return ResponseEntity.badRequest().body(
                                    new CrudResponse(false,"Do not have enough stock")
                            );
                        }else{
                            product.setMediumQty(restQty);
                        }
                        break;
                    case "L" :
                        restQty = product.getLargeQty()-orderDetail.getQuantity();
                        if(restQty<0){
                            return ResponseEntity.badRequest().body(
                                    new CrudResponse(false,"Do not have enough stock")
                            );
                        }else{
                            product.setLargeQty(restQty);
                        }
                        break;
                    default:
                        return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid size"));
                }
            }else{
                return ResponseEntity.badRequest().body(new CrudResponse(false, "Some products not found"));
            }
        }
        orderRepo.save(order);
        return ResponseEntity.ok(new CrudResponse(true, "Order placed"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable(value = "id")int orderID){
        if(orderRepo.findById(orderID).isPresent()){
            return ResponseEntity.ok(orderRepo.findById(orderID).get());
        }
        return ResponseEntity.ok(new CrudResponse(false, "Order not found"));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(){
        return ResponseEntity.ok(orderRepo.findAll());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Order>> getAllPendingOrders(){
        return ResponseEntity.ok(orderRepo.findByStatus(OrderStatusTypes.PENDING.toString()));
    }

    @GetMapping("/proccessing")
    public ResponseEntity<List<Order>> getAllProccessingOrders(){
        return ResponseEntity.ok(orderRepo.findByStatus(OrderStatusTypes.PROCESSING.toString()));
    }

    @GetMapping("/delivered")
    public ResponseEntity<List<Order>> getAllDeliveredOrders(){
        return ResponseEntity.ok(orderRepo.findByStatus(OrderStatusTypes.DELIVERED.toString()));
    }

    @GetMapping("/cancelled")
    public ResponseEntity<List<Order>> getAllCancelledOrders(){
        return ResponseEntity.ok(orderRepo.findByStatus(OrderStatusTypes.CANCELLED.toString()));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<Order>> getOrdersOf(@PathVariable(value = "id")int customerID){
        return ResponseEntity.ok(orderRepo.findByCustomerID(customerID));
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<CrudResponse> updateOrderStatus(@PathVariable(value = "id")int orderID, @PathVariable(value = "status")int status){
        if(orderRepo.findById(orderID).isPresent()){
            String sts;
            switch (status){
                case 0 : sts = OrderStatusTypes.PENDING.toString(); break;
                case 1 : sts = OrderStatusTypes.PROCESSING.toString(); break;
                case 2 : sts = OrderStatusTypes.DELIVERED.toString(); break;
                case 3 : sts = OrderStatusTypes.CANCELLED.toString(); break;
                default: return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid status"));
            }
            Order order = orderRepo.findById(orderID).get();
            order.setStatus(sts);
            orderRepo.save(order);
            return ResponseEntity.ok(new CrudResponse(true, "Order Updated"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false, "Order not found"));
    }


}
