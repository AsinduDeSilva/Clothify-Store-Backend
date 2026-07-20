package com.clothifystore.dto.response;

import com.clothifystore.enums.OrderStatusTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponseDTO {
    private int orderID;
    private LocalDate date;
    private OrderStatusTypes status;
    private String receiverAddress;
    private String receiverMobileNo;
    private String receiverName;
    private int customerID;
    private double shippingFee;
    private double total;
    private List<OrderDetailResponseDTO> orderDetails;
}
