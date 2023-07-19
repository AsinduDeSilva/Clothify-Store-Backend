package com.clothifystore.repository;

import com.clothifystore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Integer> {
}
