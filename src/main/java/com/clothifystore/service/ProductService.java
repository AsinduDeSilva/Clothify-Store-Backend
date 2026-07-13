package com.clothifystore.service;

import com.clothifystore.dto.request.ProductRequestDTO;
import com.clothifystore.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ProductService {
    void addProduct(ProductRequestDTO request) throws IOException;
    Product getProduct(int productID);
    Page<Product> getProductsByCategory(String category, int page);
    byte[] getImage(String filename);
    void updateProduct(int productID, ProductRequestDTO request);
    void updateProductImage(int productID, MultipartFile image) throws IOException;
    void deleteProduct(int productID);
    List<Product> getProducts(List<Integer> productIdList);
}
