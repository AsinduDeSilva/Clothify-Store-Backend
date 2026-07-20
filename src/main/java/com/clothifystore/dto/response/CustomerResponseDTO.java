package com.clothifystore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerResponseDTO {
    private int customerID;
    private String firstName;
    private String lastName;
    private String address;
    private String mobileNo;
    private String email;
    private List<CartItemResponseDTO> cart;
}
