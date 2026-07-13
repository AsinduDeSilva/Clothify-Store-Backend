package com.clothifystore.service;

import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.request.UpdateCustomerRequestDTO;
import com.clothifystore.entity.CartItem;
import com.clothifystore.entity.Customer;
import org.springframework.data.domain.Page;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface CustomerService {
    void addCustomer(Customer customer) throws MessagingException, UnsupportedEncodingException;
    void verifyCustomer(String email, String otp);
    void resendOTP(String email) throws MessagingException, UnsupportedEncodingException;
    boolean existsByEmail(String email) throws MessagingException, UnsupportedEncodingException;
    Customer getCustomer(int customerID);
    Customer getCustomerByEmail(String email);
    Page<Customer> getAllCustomers(int page);
    void updateCustomer(int customerID, UpdateCustomerRequestDTO request);
    List<Customer> getCustomers(List<Integer> customerIdList);
    void changePassword(int customerID, ChangePasswordRequestDTO request);
    void deleteCustomer(int customerID);
    void addToCart(int customerID, CartItem cartItem);
    void removeFromCart(int customerID, int[] indexesToRemove);
    void updateCart(int customerID, int index, CartItem cartItem);
    void emptyCart(int customerID);
    void setCart(int customerID, List<CartItem> cart);
}
