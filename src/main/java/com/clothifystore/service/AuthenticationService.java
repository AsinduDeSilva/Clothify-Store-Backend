package com.clothifystore.service;

import com.clothifystore.dto.request.AuthenticationRequestDTO;
import com.clothifystore.dto.request.AuthenticationWithOTPRequestDTO;
import com.clothifystore.dto.response.AuthenticationSuccessResponseDTO;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthenticationService {
    AuthenticationSuccessResponseDTO authenticateWithPassword(AuthenticationRequestDTO request) throws MessagingException, UnsupportedEncodingException;
    AuthenticationSuccessResponseDTO authenticateWithOtp(AuthenticationWithOTPRequestDTO request);
}
