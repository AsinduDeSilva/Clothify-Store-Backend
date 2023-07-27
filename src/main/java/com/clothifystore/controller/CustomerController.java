package com.clothifystore.controller;

import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Customer;
import com.clothifystore.enums.UserRoles;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String customerNotFound = "Customer not found";

    @PostMapping
    public ResponseEntity<CrudResponse> addCustomer(@RequestBody Customer customer){
        if(userRepo.existsByEmail(customer.getUser().getEmail())) {
            return ResponseEntity.badRequest().body(new CrudResponse(false,"Duplicate Data"));
        }
        customer.getUser().setRole(UserRoles.ROLE_CUSTOMER);
        customer.getUser().setPassword(passwordEncoder.encode(customer.getUser().getPassword()));
        customerRepo.save(customer);
        return ResponseEntity.ok(new CrudResponse(true, "Customer Added"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable(value = "id") int customerID){
        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, customerNotFound));
        }
        return ResponseEntity.ok(customerOptional.get());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCustomerByEmail(@PathVariable(value = "email")String email){
        Optional<Customer> customerOptional = customerRepo.findByUserEmail(email);
        if(customerOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, customerNotFound));
        }
        return ResponseEntity.ok(customerOptional.get());
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok(customerRepo.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateCustomer(@PathVariable(value = "id")int customerID, @RequestBody Customer customer){
        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, customerNotFound));
        }
        customer.setCustomerID(customerID);
        customer.getUser().setRole(UserRoles.ROLE_CUSTOMER);
        customer.getUser().setUserId(customerOptional.get().getUser().getUserId());
        customer.getUser().setPassword(passwordEncoder.encode(customer.getUser().getPassword()));
        customerRepo.save(customer);
        return ResponseEntity.ok(new CrudResponse(true, "Customer Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudResponse> deleteCustomer(@PathVariable (value = "id")int customerID){
        if(!customerRepo.existsById(customerID)){
            return ResponseEntity.badRequest().body(new CrudResponse(false, customerNotFound));
        }
        customerRepo.deleteById(customerID);
        return ResponseEntity.ok(new CrudResponse(true, "Customer Deleted"));
    }
}
