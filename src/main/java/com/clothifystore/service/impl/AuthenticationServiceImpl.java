package com.clothifystore.service.impl;

import com.clothifystore.service.AuthenticationService;
import com.clothifystore.service.OTPService;
import com.clothifystore.dto.request.AuthenticationRequestDTO;
import com.clothifystore.dto.request.AuthenticationWithOTPRequestDTO;
import com.clothifystore.dto.response.AuthenticationSuccessResponseDTO;
import com.clothifystore.entity.User;
import com.clothifystore.repository.UserRepo;
import com.clothifystore.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OTPService otpService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthenticationSuccessResponseDTO authenticateWithPassword(AuthenticationRequestDTO request) throws MessagingException, UnsupportedEncodingException {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException e) {
            otpService.sendOTP(user);
            throw e; // Controller will handle this and return "Need to verify account"
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        boolean isCustomer = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return new AuthenticationSuccessResponseDTO(true, true, "Login successful", jwt, isCustomer);
    }

    public AuthenticationSuccessResponseDTO authenticateWithOtp(AuthenticationWithOTPRequestDTO request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.getOtp(), user.getOtp())) {
            throw new BadCredentialsException("Invalid Credentials");
        }
        if (otpService.isOTPExpired(user)) {
            throw new BadCredentialsException("Otp Expired");
        }

        user.setOtp(null);
        user.setOtpExpirationTime(null);
        userRepo.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        boolean isCustomer = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return new AuthenticationSuccessResponseDTO(true, true, "Login successful", jwt, isCustomer);
    }
}
