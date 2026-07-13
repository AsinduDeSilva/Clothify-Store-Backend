package com.clothifystore.controller;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.request.GetCustomerByEmailReqestDTO;
import com.clothifystore.dto.request.OTPVerificationRequestDTO;
import com.clothifystore.dto.request.UpdateCustomerRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.dto.response.OTPVerificationResponseDTO;
import com.clothifystore.entity.CartItem;
import com.clothifystore.entity.Customer;
import com.clothifystore.exception.InvalidRequestException;
import com.clothifystore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<CrudResponse> addCustomer(@RequestBody Customer customer) throws MessagingException, UnsupportedEncodingException {
        customerService.addCustomer(customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CrudResponse(true, "Customer Added"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCustomer(@RequestBody OTPVerificationRequestDTO request) {
        try {
            customerService.verifyCustomer(request.getEmail(), request.getOtp());
            return ResponseEntity.ok(new OTPVerificationResponseDTO(true, false, "Verification Success"));
        } catch (InvalidRequestException e) {
            boolean isExpired = "OTP expired".equals(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new OTPVerificationResponseDTO(false, isExpired, e.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<CrudResponse> resendOTP(@RequestBody OTPVerificationRequestDTO request) throws MessagingException, UnsupportedEncodingException {
        customerService.resendOTP(request.getEmail());
        return ResponseEntity.ok(new CrudResponse(true, "OTP resent"));
    }

    @PostMapping("exists")
    public ResponseEntity<Boolean> existsByEmail(@RequestBody GetCustomerByEmailReqestDTO request) throws MessagingException, UnsupportedEncodingException {
        return ResponseEntity.ok().body(customerService.existsByEmail(request.getEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable(value = "id") int customerID) {
        return ResponseEntity.ok(customerService.getCustomer(customerID));
    }

    @PostMapping("/email")
    public ResponseEntity<?> getCustomerByEmail(@RequestBody GetCustomerByEmailReqestDTO request) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(request.getEmail()));
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<Page<Customer>> getAllCustomers(@PathVariable(value = "page") int page) {
        return ResponseEntity.ok(customerService.getAllCustomers(page));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateCustomer(@PathVariable(value = "id") int customerID,
                                                       @RequestBody UpdateCustomerRequestDTO request) {
        customerService.updateCustomer(customerID, request);
        return ResponseEntity.ok(new CrudResponse(true, "Customer Updated"));
    }

    @PostMapping("list")
    public ResponseEntity<?> getCustomers(@RequestBody List<Integer> customerIdList) {
        return ResponseEntity.ok(customerService.getCustomers(customerIdList));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<CrudResponse> changePassword(@PathVariable(value = "id") int customerID,
                                                       @RequestBody ChangePasswordRequestDTO request) {
        customerService.changePassword(customerID, request);
        return ResponseEntity.ok(new CrudResponse(true, "Password Changed"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudResponse> deleteCustomer(@PathVariable(value = "id") int customerID) {
        customerService.deleteCustomer(customerID);
        return ResponseEntity.ok(new CrudResponse(true, "Customer Deleted"));
    }

    @PostMapping("cart/{id}")
    public ResponseEntity<CrudResponse> addToCart(@PathVariable(value = "id") int customerID, @RequestBody CartItem cartItem) {
        customerService.addToCart(customerID, cartItem);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CrudResponse(true, "Added to cart"));
    }

    @DeleteMapping("cart/{id}")
    public ResponseEntity<CrudResponse> removeFromCart(@PathVariable(value = "id") int customerID,
                                                       @RequestBody int[] indexesToRemove) {
        customerService.removeFromCart(customerID, indexesToRemove);
        return ResponseEntity.ok(new CrudResponse(true, "Cart items removed"));
    }

    @PutMapping("cart/{id}")
    public ResponseEntity<CrudResponse> updateCart(@PathVariable(value = "id") int customerID,
                                                   @RequestParam(value = "index") int index,
                                                   @RequestBody CartItem cartItem) {
        customerService.updateCart(customerID, index, cartItem);
        return ResponseEntity.ok(new CrudResponse(true, "Cart updatde"));
    }

    @DeleteMapping("cart/empty/{id}")
    public ResponseEntity<CrudResponse> emptyCart(@PathVariable(value = "id") int customerID) {
        customerService.emptyCart(customerID);
        return ResponseEntity.ok(new CrudResponse(true, "Cart cleared"));
    }

    @PostMapping("cart/set/{id}")
    public ResponseEntity<CrudResponse> setCart(@PathVariable(value = "id") int customerID,
                                                @RequestBody List<CartItem> cart) {
        customerService.setCart(customerID, cart);
        return ResponseEntity.ok(new CrudResponse(true, "Cart changed"));
    }
}
