package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Customer;
import com.clothifystore.enums.UserRoles;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public ResponseEntity<CrudResponse> addCustomer(@RequestBody Customer customer){
        if(userRepo.countByUsername(customer.getUser().getUsername())==0 && userRepo.countByEmail(customer.getUser().getEmail())==0) {
            customer.getUser().setRole(UserRoles.ROLE_CUSTOMER.toString());
            customerRepo.save(customer);
            return ResponseEntity.ok(new CrudResponse(true, "Customer Added"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Duplicate Data"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable(value = "id") int customerID){
        if(customerRepo.existsById(customerID)){
            return ResponseEntity.ok(customerRepo.findByCustomerID(customerID));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Customer not found"));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok(customerRepo.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateCustomer(@PathVariable(value = "id")int customerID, @RequestBody Customer customer){
        if(customerRepo.existsById(customerID)){
            customer.setCustomerID(customerID);
            customer.getUser().setRole(UserRoles.ROLE_CUSTOMER.toString());
            customer.getUser().setUserId(customerRepo.findByCustomerID(customerID).getUser().getUserId());
            customerRepo.save(customer);
            return ResponseEntity.ok(new CrudResponse(true, "Customer Updated"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Customer not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudResponse> deleteCustomer(@PathVariable (value = "id")int customerID){
        if(customerRepo.existsById(customerID)){
            customerRepo.deleteById(customerID);
            return ResponseEntity.ok(new CrudResponse(true, "Customer Deleted"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Customer not found"));
    }
}
