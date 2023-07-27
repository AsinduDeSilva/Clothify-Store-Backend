package com.clothifystore.repository;

import com.clothifystore.entity.Product;
import com.clothifystore.enums.ProductCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {
    Page<Product> findAllByCategory(ProductCategories category, Pageable pageable);

}
