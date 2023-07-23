package com.clothifystore.entity;

import com.clothifystore.enums.ProductCategories;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productID;
    private String name;
    private double unitPrice;
    private int largeQty;
    private int mediumQty;
    private int smallQty;
    private ProductCategories category;
    private String imgFileName;
}
