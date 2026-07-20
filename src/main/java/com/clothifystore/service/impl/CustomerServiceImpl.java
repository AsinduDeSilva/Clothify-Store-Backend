package com.clothifystore.service.impl;

import com.clothifystore.service.CustomerService;
import com.clothifystore.service.OTPService;
import com.clothifystore.dto.request.CartItemRequestDTO;
import com.clothifystore.dto.request.ChangePasswordRequestDTO;
import com.clothifystore.dto.request.CustomerRegistrationRequestDTO;
import com.clothifystore.dto.request.UpdateCustomerRequestDTO;
import com.clothifystore.dto.response.CartItemResponseDTO;
import com.clothifystore.dto.response.CustomerResponseDTO;
import com.clothifystore.entity.CartItem;
import com.clothifystore.entity.Customer;
import com.clothifystore.entity.User;
import com.clothifystore.enums.UserRoles;
import com.clothifystore.exception.DuplicateResourceException;
import com.clothifystore.exception.InvalidRequestException;
import com.clothifystore.exception.ResourceNotFoundException;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final OTPService otpService;
    private final ModelMapper modelMapper;

    @PostConstruct
    private void configureModelMapper() {
        // Map Customer -> CustomerResponseDTO: pull email from nested User
        TypeMap<Customer, CustomerResponseDTO> typeMap =
                modelMapper.createTypeMap(Customer.class, CustomerResponseDTO.class);
        typeMap.addMapping(src -> src.getUser().getEmail(), CustomerResponseDTO::setEmail);
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private CustomerResponseDTO toDTO(Customer customer) {
        return modelMapper.map(customer, CustomerResponseDTO.class);
    }

    private CartItem toEntity(CartItemRequestDTO dto) {
        return modelMapper.map(dto, CartItem.class);
    }

    // ── Write operations ──────────────────────────────────────────────────────

    public void addCustomer(CustomerRegistrationRequestDTO request)
            throws MessagingException, UnsupportedEncodingException {

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Duplicate Data");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRoles.ROLE_CUSTOMER);
        user.setEnabled(false);

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAddress(request.getAddress());
        customer.setMobileNo(request.getMobileNo());
        customer.setUser(user);

        customerRepo.save(customer);
        otpService.sendOTP(user);
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

    public void updateCustomer(int customerID, UpdateCustomerRequestDTO request) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAddress(request.getAddress());
        customer.setMobileNo(request.getMobileNo());
        customerRepo.save(customer);
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

    // ── Read operations ───────────────────────────────────────────────────────

    public CustomerResponseDTO getCustomer(int customerID) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return toDTO(customer);
    }

    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer customer = customerRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return toDTO(customer);
    }

    public Page<CustomerResponseDTO> getAllCustomers(int page) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        return customerRepo.findAll(pageable).map(this::toDTO);
    }

    public List<CustomerResponseDTO> getCustomers(List<Integer> customerIdList) {
        return customerRepo.findAllByCustomerIDIn(customerIdList)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Cart operations ───────────────────────────────────────────────────────

    public void addToCart(int customerID, CartItemRequestDTO cartItemDTO) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.getCart().add(toEntity(cartItemDTO));
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

    public void updateCart(int customerID, int index, CartItemRequestDTO cartItemDTO) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.getCart().set(index, toEntity(cartItemDTO));
        customerRepo.save(customer);
    }

    public void emptyCart(int customerID) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.getCart().clear();
        customerRepo.save(customer);
    }

    public void setCart(int customerID, List<CartItemRequestDTO> cartDTOs) {
        Customer customer = customerRepo.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<CartItem> cart = cartDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        customer.setCart(cart);
        customerRepo.save(customer);
    }
}
