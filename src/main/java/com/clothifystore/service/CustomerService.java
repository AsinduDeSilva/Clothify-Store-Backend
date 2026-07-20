package com.clothifystore.service;

import com.clothifystore.dto.request.CartItemRequestDTO;
import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.request.CustomerRegistrationRequestDTO;
import com.clothifystore.dto.request.UpdateCustomerRequestDTO;
import com.clothifystore.dto.response.CustomerResponseDTO;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface CustomerService {
    void addCustomer(CustomerRegistrationRequestDTO request) throws MessagingException, UnsupportedEncodingException;
    void verifyCustomer(String email, String otp);
    void resendOTP(String email) throws MessagingException, UnsupportedEncodingException;
    boolean existsByEmail(String email) throws MessagingException, UnsupportedEncodingException;
    CustomerResponseDTO getCustomer(int customerID);
    CustomerResponseDTO getCustomerByEmail(String email);
    Page<CustomerResponseDTO> getAllCustomers(int page);
    void updateCustomer(int customerID, UpdateCustomerRequestDTO request);
    List<CustomerResponseDTO> getCustomers(List<Integer> customerIdList);
    void changePassword(int customerID, ChangePasswordRequestDTO request);
    void deleteCustomer(int customerID);
    void addToCart(int customerID, CartItemRequestDTO cartItem);
    void removeFromCart(int customerID, int[] indexesToRemove);
    void updateCart(int customerID, int index, CartItemRequestDTO cartItem);
    void emptyCart(int customerID);
    void setCart(int customerID, List<CartItemRequestDTO> cart);
}
