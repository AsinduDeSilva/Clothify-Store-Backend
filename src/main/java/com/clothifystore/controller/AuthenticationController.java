package com.clothifystore.controller;

import com.clothifystore.dto.request.AuthenticationRequestDTO;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @PostMapping
    public ResponseEntity<?> authentication(@RequestBody AuthenticationRequestDTO request) throws MessagingException, UnsupportedEncodingException {

        Optional<User> user = userRepo.findByEmail(request.getEmail());
        if(user.isPresent() && !user.get().isEnabled()){
            otpService.sendOTP(user.get());
            return ResponseEntity.status(HttpStatus.LOCKED).body(
                    new AuthenticationFailedResponseDTO(false, "Need to verify account")
            );
        }

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
            );
        }catch(BadCredentialsException e){
            return ResponseEntity.badRequest().body(new AuthenticationFailedResponseDTO(false, "Invalid Credentials"));
        }


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        boolean isCustomer = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return ResponseEntity.ok(
                new AuthenticationSuccessResponseDTO(true, "Login successful", jwt, isCustomer)
        );
    }
}
