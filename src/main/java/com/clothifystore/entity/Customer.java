package com.clothifystore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerID;

    private String firstName;
    private String lastName;
    private String address;
    private String mobileNo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CustomerID", referencedColumnName = "customerID")
    private List<Order> orderList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;



}
