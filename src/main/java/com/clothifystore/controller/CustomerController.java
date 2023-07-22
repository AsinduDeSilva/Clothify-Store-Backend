package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Customer;
import com.clothifystore.entity.User;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer){
        if(userRepo.countByUsername(customer.getUser().getUsername())==0 && userRepo.countByEmail(customer.getUser().getEmail())==0) {
            customer.getUser().setRole("ROLE_CUSTOMER");
            customerRepo.save(customer);
            return ResponseEntity.ok(new CrudResponse(true, "Customer Added"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Duplicate Data"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable (value = "id") int customerID){
        if(customerRepo.existsById(customerID)){
            return ResponseEntity.ok(customerRepo.findByCustomerID(customerID));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Customer not found"));
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers(){
        return ResponseEntity.ok(customerRepo.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable(value = "id")int customerID, @RequestBody Customer customer){
        if(customerRepo.existsById(customerID)){
            Customer customer1 = customerRepo.findByCustomerID(customerID);
            customer.setCustomerID(customer1.getCustomerID());
            //customer.setOrderList(customer1.getOrderList());
            customer.getUser().setRole(customer1.getUser().getRole());
            customer.getUser().setUserId(customer1.getUser().getUserId());
            customerRepo.save(customer);

            return ResponseEntity.ok(new CrudResponse(true, "Customer Updated"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Customer not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable (value = "id")int customerID){
        if(customerRepo.existsById(customerID)){
            customerRepo.deleteById(customerID);
            return ResponseEntity.ok(new CrudResponse(true, "Customer Deleted"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Customer not found"));
    }
}
