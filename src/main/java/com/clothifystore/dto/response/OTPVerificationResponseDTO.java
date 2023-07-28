package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OTPVerificationResponseDTO {
    private boolean isSuccess;
    private boolean isOtpExpired;
    private String message;
}
