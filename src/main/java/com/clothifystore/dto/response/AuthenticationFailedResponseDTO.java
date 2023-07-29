package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticationFailedResponseDTO {
    private boolean isSuccess;
    private boolean isCredentialsValid;
    private String message;
}
