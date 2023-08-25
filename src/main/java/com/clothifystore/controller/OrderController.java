package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.dto.response.OrderStatsResponseDTO;
import com.clothifystore.entity.Customer;
import com.clothifystore.entity.Order;
import com.clothifystore.entity.OrderDetail;
import com.clothifystore.entity.Product;
import com.clothifystore.enums.OrderStatusTypes;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.OrderRepo;
import com.clothifystore.repository.ProductRepo;
import com.clothifystore.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;


import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<CrudResponse> addOrder(@RequestBody Order order) throws MessagingException, UnsupportedEncodingException {

        for (OrderDetail orderDetail : order.getOrderDetails()){

            Optional<Product> productOptional = productRepo.findById(orderDetail.getProductID());
            if(productOptional.isEmpty()){
                return ResponseEntity.badRequest().body(
                        new CrudResponse(false, "Some products not found")
                );
            }

            Product product = productOptional.get();
            int restQty;
            switch (orderDetail.getSize()) {
                case SMALL: restQty = product.getSmallQty() - orderDetail.getQuantity();break;
                case MEDIUM: restQty = product.getMediumQty() - orderDetail.getQuantity();break;
                case LARGE: restQty = product.getLargeQty() - orderDetail.getQuantity();break;
                default:
                    return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid size"));
            }

            if (restQty < 0) {
                return ResponseEntity.badRequest().body(
                        new CrudResponse(false, "Do not have enough stock")
                );
            }

            switch (orderDetail.getSize()) {
                case SMALL: product.setSmallQty(restQty);break;
                case MEDIUM: product.setMediumQty(restQty);break;
                case LARGE: product.setLargeQty(restQty);
            }
        }

        order.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        order.setStatus(OrderStatusTypes.PENDING);
        orderRepo.save(order);

        Optional<Customer> customerOptional = customerRepo.findById(order.getCustomerID());
        if(customerOptional.isPresent()){
            String emailBody = "<h1>Hey there, "+customerOptional.get().getFirstName()+"</h1>"
                             + "Your order Received. Thank you for shopping with us.";

            emailService.sendEmail(customerOptional.get().getUser().getEmail(), "Order Placed", emailBody);
        }

        return ResponseEntity.ok(new CrudResponse(true, "Order placed"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable(value = "id")int orderID){
        Optional<Order> orderOptional = orderRepo.findById(orderID);
        if(orderOptional.isPresent()){
            return ResponseEntity.ok(orderOptional.get());
        }
        return ResponseEntity.ok(new CrudResponse(false, "Order not found"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable(value = "status") int status,
                                               @RequestParam(value = "page") int page){

        Sort sort = Sort.by(Sort.Order.desc("orderID"));
        Pageable pageable = PageRequest.of(page - 1, 16, sort);
        switch (status){
            case 0 :
                return ResponseEntity.ok(orderRepo.findAll(pageable));
            case 1 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.PENDING, pageable));
            case 2 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.PROCESSING, pageable));
            case 3 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.OUT_FOR_DELIVERY, pageable));
            case 4 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.DELIVERED, pageable));
            case 5 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.CANCELLED, pageable));
            default:
                return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid status"));
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Page<Order>> getOrdersOf(@PathVariable(value = "id")int customerID,
                                                   @RequestParam(value = "page") int page){
        Sort sort = Sort.by(Sort.Order.desc("orderID"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);
        return ResponseEntity.ok(orderRepo.findByCustomerID(customerID, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateOrderStatus(@PathVariable(value = "id") int orderID,
                                                          @RequestParam(value = "status" )int status) throws MessagingException, UnsupportedEncodingException {

        Optional<Order> orderOptional = orderRepo.findById(orderID);
        if(orderOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, "Order not found"));
        }
        switch (status){
            case 0 : orderOptional.get().setStatus(OrderStatusTypes.PENDING); break;
            case 1 : orderOptional.get().setStatus(OrderStatusTypes.PROCESSING); break;
            case 2 : orderOptional.get().setStatus(OrderStatusTypes.OUT_FOR_DELIVERY); break;
            case 3 : orderOptional.get().setStatus(OrderStatusTypes.DELIVERED); break;
            case 4 : orderOptional.get().setStatus(OrderStatusTypes.CANCELLED); break;
            default:
                return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid status"));
        }
        orderRepo.save(orderOptional.get());

        Optional<Customer> customerOptional = customerRepo.findById(orderOptional.get().getCustomerID());
        if(customerOptional.isPresent()){
            String body = "<h1>Hey there, "+customerOptional.get().getFirstName()+"</h1>"
                    + "Your order is "+orderOptional.get().getStatus().toString().toLowerCase().replace('_',' ');

            emailService.sendEmail(customerOptional.get().getUser().getEmail(), "Order Status Updated", body);
        }

        return ResponseEntity.ok(new CrudResponse(true, "Order Updated"));

    }

    @GetMapping("week")
    public ResponseEntity<List<Integer>> getTotalOrdersCountOfPast7Days(){
        List<Integer> orderCountList = new ArrayList<>();
        for (int i = 7; i > 0; i--){
            orderCountList.add(orderRepo.countByDate(LocalDate.now().minusDays(i)));
        }
        return ResponseEntity.ok(orderCountList);
    }

    @GetMapping("stats")
    public ResponseEntity<?> getSalesIncome(){
        List<Order> ordersOfToday = orderRepo.findByDate(LocalDate.now());
        List<Order> ordersOfYesterday = orderRepo.findByDate(LocalDate.now().minusDays(1));
        List<Order> ordersOfLast30Days = orderRepo.findByDateAfter(LocalDate.now().minusMonths(1));

        double incomeOfToday = 0;
        double incomeOfYesterday = 0;
        double incomeOfLast30Days = 0;

        for (Order order : ordersOfToday) {
            if(order.getStatus() == OrderStatusTypes.CANCELLED){
               continue;
            }
            incomeOfToday += order.getTotal();
        }

        for (Order order : ordersOfYesterday) {
            if(order.getStatus() == OrderStatusTypes.CANCELLED){
               continue;
            }
            incomeOfYesterday += order.getTotal();
        }

        for (Order order : ordersOfLast30Days) {
            if(order.getStatus() == OrderStatusTypes.CANCELLED){
                continue;
            }
            incomeOfLast30Days += order.getTotal();
        }

        return ResponseEntity.ok(new OrderStatsResponseDTO(
                incomeOfToday,
                incomeOfYesterday,
                incomeOfLast30Days,
                orderRepo.countByStatus(OrderStatusTypes.PENDING),
                orderRepo.countByStatus(OrderStatusTypes.PROCESSING),
                orderRepo.countByStatus(OrderStatusTypes.OUT_FOR_DELIVERY)
        ));
    }

}
