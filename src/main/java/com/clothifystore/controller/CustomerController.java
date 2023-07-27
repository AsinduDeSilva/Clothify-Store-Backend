package com.clothifystore.controller;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.request.GetCustomerByEmailReqestDTO;
import com.clothifystore.dto.request.UpdateCustomerRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Customer;
import com.clothifystore.enums.UserRoles;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/email")
    public ResponseEntity<?> getCustomerByEmail(@RequestBody GetCustomerByEmailReqestDTO request){

        Optional<Customer> customerOptional = customerRepo.findByUserEmail(request.getEmail());
        if(customerOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, customerNotFound));
        }
        return ResponseEntity.ok(customerOptional.get());
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<Page<Customer>> getAllCustomers(@PathVariable(value = "page") int page){
        Pageable pageable = PageRequest.of(page - 1, 20);
        return ResponseEntity.ok(customerRepo.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateCustomer(@PathVariable(value = "id") int customerID,
                                                       @RequestBody UpdateCustomerRequestDTO reques){

        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, customerNotFound));
        }
        customerOptional.get().setFirstName(reques.getFirstName());
        customerOptional.get().setLastName(reques.getLastName());
        customerOptional.get().setAddress(reques.getAddress());
        customerOptional.get().setMobileNo(reques.getMobileNo());
        customerRepo.save(customerOptional.get());
        return ResponseEntity.ok(new CrudResponse(true, "Customer Updated"));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<CrudResponse> changePassword(@PathVariable(value = "id") int customerID,
                                                       @RequestBody ChangePasswordRequestDTO request){

        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, customerNotFound));
        }
        customerOptional.get().getUser().setPassword(passwordEncoder.encode(request.getPassword()));
        customerRepo.save(customerOptional.get());
        return ResponseEntity.ok(new CrudResponse(true, "Password Changed"));
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
