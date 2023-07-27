package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Customer;
import com.clothifystore.entity.Order;
import com.clothifystore.entity.OrderDetail;
import com.clothifystore.entity.Product;
import com.clothifystore.enums.OrderStatusTypes;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.OrderRepo;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderMail;

    @PostMapping
    public ResponseEntity<CrudResponse> addOrder(@RequestBody Order order) throws MessagingException {

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

        order.setStatus(OrderStatusTypes.PENDING);
        orderRepo.save(order);

        sendConfirmationEmail(order);

        return ResponseEntity.ok(new CrudResponse(true, "Order placed"));

    }

    private void sendConfirmationEmail(Order order) throws MessagingException {

        Optional<Customer> customerOptional = customerRepo.findById(order.getCustomerID());
        if(customerOptional.isEmpty()){return;}
        Customer customer = customerOptional.get();

        String emailBody = "<h1>Hey there, "+customer.getFirstName()+" "+customer.getLastName()+"</h1>"
                          +"Your order Received. Thank you for shopping with us.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(senderMail);
        helper.setSubject("Order Placed");
        helper.setTo(customer.getUser().getEmail());
        helper.setText(emailBody,true);
        javaMailSender.send(message);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable(value = "id")int orderID){
        Optional<Order> orderOptional = orderRepo.findById(orderID);
        if(orderOptional.isPresent()){
            return ResponseEntity.ok(orderOptional.get());
        }
        return ResponseEntity.ok(new CrudResponse(false, "Order not found"));
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<?> getAllOrdersByPage(@PathVariable(value = "page")int page){

        Sort sort = Sort.by(Sort.Order.desc("orderID"));
        Pageable pageable = PageRequest.of(page - 1, 20, sort);
        return ResponseEntity.ok(orderRepo.findAll(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable(value = "status") int status,
                                               @RequestParam(value = "page") int page){

        Sort sort = Sort.by(Sort.Order.desc("orderID"));
        Pageable pageable = PageRequest.of(page - 1, 20, sort);
        switch (status){
            case 0 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.PENDING, pageable));
            case 1 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.PROCESSING, pageable));
            case 2 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.OUT_FOR_DELIVERY, pageable));
            case 3 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.DELIVERED, pageable));
            case 4 :
                return ResponseEntity.ok(orderRepo.findAllByStatus(OrderStatusTypes.CANCELLED, pageable));
            default:
                return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid status"));
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<Order>> getOrdersOf(@PathVariable(value = "id")int customerID){
        return ResponseEntity.ok(orderRepo.findByCustomerID(customerID));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateOrderStatus(@PathVariable(value = "id") int orderID,
                                                          @RequestParam(value = "status" )int status){

        Optional<Order> orderOptional = orderRepo.findById(orderID);
        if(orderOptional.isPresent()){
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
            return ResponseEntity.ok(new CrudResponse(true, "Order Updated"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false, "Order not found"));
    }


}
