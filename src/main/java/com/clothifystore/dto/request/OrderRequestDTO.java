package com.clothifystore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderRequestDTO {
    private int customerID;
    private String receiverName;
    private String receiverAddress;
    private String receiverMobileNo;
    private double shippingFee;
    private double total;
    private List<OrderDetailRequestDTO> orderDetails;
}
