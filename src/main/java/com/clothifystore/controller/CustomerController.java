package com.clothifystore.controller;

import com.clothifystore.dto.response.ErrorResponse;
import com.clothifystore.entity.Customer;
import com.clothifystore.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepo customerRepo;

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer){
        if(customerRepo.countAllByEmail(customer.getEmail())==0) {
            return ResponseEntity.ok(customerRepo.save(customer));
        }else{
            return ResponseEntity.badRequest().body(new ErrorResponse("Email already exists"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable (value = "id") int customerID){
        if(customerRepo.findById(customerID).isPresent()){
            return ResponseEntity.ok(customerRepo.findById(customerID).get());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Customer not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable(value = "id")int customerID, @RequestBody Customer customer){
        if(customerRepo.findById(customerID).isPresent()){
            customer.setCustomerID(customerID);
            return ResponseEntity.ok(customerRepo.save(customer));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Customer not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable (value = "id")int customerID){
        if(customerRepo.findById(customerID).isPresent()){
            customerRepo.deleteById(customerID);
            return ResponseEntity.ok("deleted");
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Customer not found"));
    }
}
