package com.clothifystore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateCustomerRequestDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String mobileNo;
}
