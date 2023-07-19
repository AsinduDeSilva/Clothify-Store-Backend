package com.clothifystore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(unique = true)
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String mobileNo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CustomerID", referencedColumnName = "CustomerID")
    private List<Order> orderList;



}
