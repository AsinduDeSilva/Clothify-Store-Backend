package com.clothifystore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticationWithOTPRequestDTO {
    private String email;
    private String otp;
}
