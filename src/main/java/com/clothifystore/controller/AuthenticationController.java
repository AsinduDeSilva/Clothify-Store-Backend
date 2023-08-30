package com.clothifystore.controller;

import com.clothifystore.dto.request.AuthenticationRequestDTO;
import com.clothifystore.dto.request.AuthenticationWithOTPRequestDTO;
import com.clothifystore.dto.response.AuthenticationFailedResponseDTO;
import com.clothifystore.dto.response.AuthenticationSuccessResponseDTO;
import com.clothifystore.entity.User;
import com.clothifystore.repository.UserRepo;
import com.clothifystore.security.util.JwtUtil;
import com.clothifystore.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

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

    @PostMapping
    public ResponseEntity<?> authenticateWithPassword(@RequestBody AuthenticationRequestDTO request)
            throws MessagingException, UnsupportedEncodingException {

        Optional<User> userOptional = userRepo.findByEmail(request.getEmail());
        if (userOptional.isPresent() && !passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, false, "Invalid Credentials"));
        }

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
            );
        }catch(BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, false, "Invalid Credentials"));
        }catch(DisabledException e){
            otpService.sendOTP(userOptional.get());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, true, "Need to verify account"));
        }


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        boolean isCustomer = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return ResponseEntity.ok(
                new AuthenticationSuccessResponseDTO(true, true, "Login successful", jwt, isCustomer)
        );
    }

    @PostMapping("/otp")
    public ResponseEntity<?> authenticateWithOtp(@RequestBody AuthenticationWithOTPRequestDTO request){
        Optional<User> userOptional = userRepo.findByEmail(request.getEmail());
        if(userOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, false, "Invalid Credentials"));
        }
        User user = userOptional.get();
        if(!passwordEncoder.matches(request.getOtp(), user.getOtp())){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, false, "Invalid Credentials"));
        }
        if(otpService.isOTPExpired(user)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, true, "Otp Expired"));
        }

        user.setOtp(null);
        user.setOtpExpirationTime(null);
        userRepo.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        boolean isCustomer = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return ResponseEntity.ok(
                new AuthenticationSuccessResponseDTO(true, true, "Login successful", jwt, isCustomer)
        );

    }


}
