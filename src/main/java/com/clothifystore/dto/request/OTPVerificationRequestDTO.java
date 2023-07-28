package com.clothifystore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OTPVerificationRequestDTO {
    private String email;
    private String otp;
}
