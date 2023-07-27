package com.clothifystore.controller;

import com.clothifystore.dto.request.AuthenticationRequestDTO;
import com.clothifystore.dto.response.AuthenticationFailedResponseDTO;
import com.clothifystore.dto.response.AuthenticationSuccessResponseDTO;
import com.clothifystore.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> authentication(@RequestBody AuthenticationRequestDTO request){

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
            );
        }catch(BadCredentialsException e){
            return ResponseEntity.badRequest().body(
                    new AuthenticationFailedResponseDTO(false, "Invalid Credentials")
            );
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        boolean isCustomer = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return ResponseEntity.ok(
                new AuthenticationSuccessResponseDTO(true, "Login successful", jwt, isCustomer)
        );
    }
}
