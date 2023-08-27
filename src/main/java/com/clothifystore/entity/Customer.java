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

    private String firstName;
    private String lastName;
    private String address;
    private String mobileNo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customerID", referencedColumnName = "customerID")
    private List<CartItem> cart;



}
