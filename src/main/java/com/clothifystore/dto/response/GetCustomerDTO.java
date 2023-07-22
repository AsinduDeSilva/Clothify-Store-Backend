package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetCustomerDTO {
    private int customerID;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String mobileNo;
    private String orderList;
}
