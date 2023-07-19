package com.clothifystore.controller;

import com.clothifystore.entity.Product;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final String PRODUCT_IMAGES_FOLDER_PATH = "C:\\Asindu\\Clothify Store\\Product Images\\";

    @Autowired
    private ProductRepo productRepo;

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product){
        return ResponseEntity.ok(productRepo.save(product));
    }
}
