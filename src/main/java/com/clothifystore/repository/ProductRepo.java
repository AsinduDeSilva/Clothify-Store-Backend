package com.clothifystore.repository;

import com.clothifystore.entity.Product;
import com.clothifystore.enums.ProductCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {
    List<Product> findByCategory(ProductCategories category);

}
