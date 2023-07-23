package com.clothifystore.entity;

import com.clothifystore.enums.ProductSizes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int productID;
    private ProductSizes size;
    private int quantity;
    private double unitPrice;

}
