package com.clothifystore.entity;

import com.clothifystore.enums.OrderStatusTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "customer_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderID;
    private String dateAndTime;
    private OrderStatusTypes status;
    private String receiverAddress;
    private String receiverMobileNo;
    private String receiverName;
    private int customerID;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderID", referencedColumnName = "orderID")
    private List<OrderDetail> orderDetails;


}
