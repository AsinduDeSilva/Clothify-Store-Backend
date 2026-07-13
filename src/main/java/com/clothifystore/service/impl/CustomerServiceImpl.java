package com.clothifystore.service.impl;

import com.clothifystore.service.CustomerService;
import com.clothifystore.service.OTPService;
import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.request.UpdateCustomerRequestDTO;
import com.clothifystore.entity.CartItem;
import com.clothifystore.entity.Customer;
import com.clothifystore.entity.User;
import com.clothifystore.enums.UserRoles;
import com.clothifystore.exception.DuplicateResourceException;
import com.clothifystore.exception.InvalidRequestException;
import com.clothifystore.exception.ResourceNotFoundException;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    public void addCustomer(Customer customer) throws MessagingException, UnsupportedEncodingException {
        if (userRepo.existsByEmail(customer.getUser().getEmail())) {
            throw new DuplicateResourceException("Duplicate Data");
        }
        customer.getUser().setEnabled(false);
        customer.getUser().setRole(UserRoles.ROLE_CUSTOMER);
        customer.getUser().setPassword(passwordEncoder.encode(customer.getUser().getPassword()));
        customerRepo.save(customer);

        otpService.sendOTP(customer.getUser());
    }

    public void verifyCustomer(String email, String otp) {
        Customer customer = customerRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        User user = customer.getUser();

        if (otpService.isOTPExpired(user)) {
            throw new InvalidRequestException("OTP expired");
        }

        if (!otpService.verifyOTP(user, otp)) {
            throw new InvalidRequestException("Invalid OTP");
        }
        user.setEnabled(true);
        userRepo.save(user);
    }

    public void resendOTP(String email) throws MessagingException, UnsupportedEncodingException {
        Customer customer = customerRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        otpService.sendOTP(customer.getUser());
    }

    public boolean existsByEmail(String email) throws MessagingException, UnsupportedEncodingException {
        boolean exists = customerRepo.existsByUser_Email(email);
        if (exists) {
            otpService.sendOTP(userRepo.findByEmail(email).get());
        }
        return exists;
    }

    public Customer getCustomer(int customerID) {
        return customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public Page<Customer> getAllCustomers(int page) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        return customerRepo.findAll(pageable);
    }

    public void updateCustomer(int customerID, UpdateCustomerRequestDTO request) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAddress(request.getAddress());
        customer.setMobileNo(request.getMobileNo());
        customerRepo.save(customer);
    }

    public List<Customer> getCustomers(List<Integer> customerIdList) {
        return customerRepo.findAllByCustomerIDIn(customerIdList);
    }

    public void changePassword(int customerID, ChangePasswordRequestDTO request) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.getUser().setPassword(passwordEncoder.encode(request.getPassword()));
        customerRepo.save(customer);
    }

    public void deleteCustomer(int customerID) {
        if (!customerRepo.existsById(customerID)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        customerRepo.deleteById(customerID);
    }

    public void addToCart(int customerID, CartItem cartItem) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.getCart().add(cartItem);
        customerRepo.save(customer);
    }

    public void removeFromCart(int customerID, int[] indexesToRemove) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        for (int i = indexesToRemove.length - 1; i >= 0; i--) {
            customer.getCart().remove(indexesToRemove[i]);
        }
        customerRepo.save(customer);
    }

    public void updateCart(int customerID, int index, CartItem cartItem) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.getCart().set(index, cartItem);
        customerRepo.save(customer);
    }

    public void emptyCart(int customerID) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.getCart().clear();
        customerRepo.save(customer);
    }

    public void setCart(int customerID, List<CartItem> cart) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.setCart(cart);
        customerRepo.save(customer);
    }
}
