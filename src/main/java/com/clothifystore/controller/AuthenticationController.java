package com.clothifystore.controller;

import com.clothifystore.dto.request.AuthenticationRequestDTO;
import com.clothifystore.dto.request.AuthenticationWithOTPRequestDTO;
import com.clothifystore.dto.response.AuthenticationFailedResponseDTO;
import com.clothifystore.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> authenticateWithPassword(@RequestBody AuthenticationRequestDTO request)
            throws MessagingException, UnsupportedEncodingException {
        try {
            return ResponseEntity.ok(authenticationService.authenticateWithPassword(request));
        } catch (DisabledException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, true, "Need to verify account"));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, false, "Invalid Credentials"));
        }
    }

    @PostMapping("/otp")
    public ResponseEntity<?> authenticateWithOtp(@RequestBody AuthenticationWithOTPRequestDTO request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticateWithOtp(request));
        } catch (BadCredentialsException e) {
            boolean isOtpExpired = "Otp Expired".equals(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationFailedResponseDTO(false, isOtpExpired, e.getMessage()));
        }
    }
}
