package com.clothifystore.controller;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.request.GetCustomerByEmailReqestDTO;
import com.clothifystore.dto.request.OTPVerificationRequestDTO;
import com.clothifystore.dto.request.UpdateCustomerRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.dto.response.OTPVerificationResponseDTO;
import com.clothifystore.entity.CartItem;
import com.clothifystore.entity.Customer;
import com.clothifystore.entity.User;
import com.clothifystore.enums.UserRoles;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.UserRepo;
import com.clothifystore.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
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

    @Autowired
    private OTPService otpService;

    private final String customerNotFound = "Customer not found";

    @PostMapping
    public ResponseEntity<CrudResponse> addCustomer(@RequestBody Customer customer) throws MessagingException, UnsupportedEncodingException {

        if(userRepo.existsByEmail(customer.getUser().getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new CrudResponse(false,"Duplicate Data"));
        }
        customer.getUser().setEnabled(false);
        customer.getUser().setRole(UserRoles.ROLE_CUSTOMER);
        customer.getUser().setPassword(passwordEncoder.encode(customer.getUser().getPassword()));
        customerRepo.save(customer);

        otpService.sendOTP(customer.getUser());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CrudResponse(true, "Customer Added"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCustomer(@RequestBody OTPVerificationRequestDTO request){

        Optional<Customer> customerOptional = customerRepo.findByUserEmail(request.getEmail());
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        User user = customerOptional.get().getUser();

        if(otpService.isOTPExpired(user)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new OTPVerificationResponseDTO(false, true, "OTP expired"));
        }

        if (!otpService.verifyOTP(user, request.getOtp())){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new OTPVerificationResponseDTO(false, false, "Invalid OTP"));
        }
        user.setEnabled(true);
        userRepo.save(user);

        return ResponseEntity.ok(new OTPVerificationResponseDTO(true,false, "Verification Success"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<CrudResponse> resendOTP(@RequestBody OTPVerificationRequestDTO request) throws MessagingException, UnsupportedEncodingException {

        Optional<Customer> customerOptional = customerRepo.findByUserEmail(request.getEmail());
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        otpService.sendOTP(customerOptional.get().getUser());
        return ResponseEntity.ok(new CrudResponse(true, "OTP resent"));
    }

    @PostMapping("exists")
    public  ResponseEntity<Boolean> existsByEmail(@RequestBody GetCustomerByEmailReqestDTO request) throws MessagingException, UnsupportedEncodingException {
        Boolean exists = customerRepo.existsByUser_Email(request.getEmail());
        if(exists){
            otpService.sendOTP(userRepo.findByEmail(request.getEmail()).get());
        }
        return ResponseEntity.ok().body(exists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable(value = "id") int customerID){

        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        return ResponseEntity.ok(customerOptional.get());
    }

    @PostMapping("/email")
    public ResponseEntity<?> getCustomerByEmail(@RequestBody GetCustomerByEmailReqestDTO request){

        Optional<Customer> customerOptional = customerRepo.findByUserEmail(request.getEmail());
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
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
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        customerOptional.get().setFirstName(reques.getFirstName());
        customerOptional.get().setLastName(reques.getLastName());
        customerOptional.get().setAddress(reques.getAddress());
        customerOptional.get().setMobileNo(reques.getMobileNo());
        customerRepo.save(customerOptional.get());
        return ResponseEntity.ok(new CrudResponse(true, "Customer Updated"));
    }

    @PostMapping("list")
    public ResponseEntity<?> getCustomers(@RequestBody List<Integer> customerIdList){
        return ResponseEntity.ok(customerRepo.findAllByCustomerIDIn(customerIdList));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<CrudResponse> changePassword(@PathVariable(value = "id") int customerID,
                                                       @RequestBody ChangePasswordRequestDTO request){

        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        customerOptional.get().getUser().setPassword(passwordEncoder.encode(request.getPassword()));
        customerRepo.save(customerOptional.get());
        return ResponseEntity.ok(new CrudResponse(true, "Password Changed"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudResponse> deleteCustomer(@PathVariable (value = "id")int customerID){

        if(!customerRepo.existsById(customerID)){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        customerRepo.deleteById(customerID);
        return ResponseEntity.ok(new CrudResponse(true, "Customer Deleted"));
    }

    @PostMapping("cart/{id}")
    public ResponseEntity<CrudResponse> addToCart(@PathVariable(value = "id") int customerID, @RequestBody CartItem cartItem){
        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        Customer customer = customerOptional.get();
        customer.getCart().add(cartItem);
        customerRepo.save(customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CrudResponse(true, "Added to cart"));
    }

    @DeleteMapping("cart/{id}")
    public ResponseEntity<CrudResponse> removeFromCart(@PathVariable(value = "id") int customerID,
                                            @RequestBody int[] indexesToRemove){

        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        Customer customer = customerOptional.get();

        for (int i = indexesToRemove.length - 1; i >= 0; i--) {
            customer.getCart().remove(indexesToRemove[i]);
        }
        customerRepo.save(customer);
        return ResponseEntity.ok(new CrudResponse(true, "Cart items removed"));
    }

    @PutMapping("cart/{id}")
    public ResponseEntity<CrudResponse> updateCart(@PathVariable(value = "id") int customerID,
                                                   @RequestParam(value = "index") int index,
                                                   @RequestBody CartItem cartItem){

        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        Customer customer = customerOptional.get();
        customer.getCart().set(index, cartItem);
        customerRepo.save(customer);
        return ResponseEntity.ok(new CrudResponse(true, "Cart updatde"));
    }

    @DeleteMapping("cart/empty/{id}")
    public ResponseEntity<CrudResponse> emptyCart(@PathVariable(value = "id") int customerID){
        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        Customer customer = customerOptional.get();
        customer.getCart().clear();
        customerRepo.save(customer);
        return ResponseEntity.ok(new CrudResponse(true, "Cart cleared"));
    }

    @PostMapping("cart/set/{id}")
    public ResponseEntity<CrudResponse> setCart(@PathVariable(value = "id") int customerID,
                                                @RequestBody List<CartItem> cart){

        Optional<Customer> customerOptional = customerRepo.findById(customerID);
        if(customerOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new CrudResponse(false, customerNotFound));
        }
        Customer customer = customerOptional.get();
        customer.setCart(cart);
        customerRepo.save(customer);
        return ResponseEntity.ok(new CrudResponse(true, "Cart changed"));
    }

}
