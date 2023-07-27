package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticationSuccessResponseDTO {
    private boolean isSuccess;
    private String message;
    private String jwt;
    private boolean isCustomer;
}
