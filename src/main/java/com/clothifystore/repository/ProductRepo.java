package com.clothifystore.repository;

import com.clothifystore.entity.Product;
import com.clothifystore.enums.ProductCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product,Integer> {
    List<Product> findByCategory(ProductCategories category);

}
