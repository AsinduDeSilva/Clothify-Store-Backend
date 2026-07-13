package com.clothifystore.service.impl;

import com.clothifystore.service.ProductService;

import com.clothifystore.dto.request.ProductRequestDTO;
import com.clothifystore.entity.Product;
import com.clothifystore.enums.ProductCategories;
import com.clothifystore.exception.InvalidRequestException;
import com.clothifystore.exception.ResourceNotFoundException;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Value("${product.images.path}")
    private String productImagesPath;

    @Autowired
    private ProductRepo productRepo;

    public void addProduct(ProductRequestDTO request) throws IOException {
        int productID = productRepo.save(new Product()).getProductID();
        request.getImage().transferTo(new File(productImagesPath + productID + ".png"));

        productRepo.save(new Product(productID, request.getName(), request.getUnitPrice(), request.getLargeQty()
                , request.getMediumQty(), request.getSmallQty(), request.getCategory(), productID + ".png")
        );
    }

    public Product getProduct(int productID) {
        return productRepo.findById(productID)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Page<Product> getProductsByCategory(String category, int page) {
        Sort sort = Sort.by(Sort.Order.desc("productID"));
        Pageable pageable = PageRequest.of(page - 1, 16, sort);
        switch (category) {
            case "all":
                return productRepo.findAll(pageable);
            case "men":
                return productRepo.findAllByCategory(ProductCategories.MEN, pageable);
            case "women":
                return productRepo.findAllByCategory(ProductCategories.WOMEN, pageable);
            case "kids":
                return productRepo.findAllByCategory(ProductCategories.KIDS, pageable);
            case "accessories":
                return productRepo.findAllByCategory(ProductCategories.ACCESSORIES, pageable);
            default:
                throw new InvalidRequestException("Invalid category");
        }
    }

    public byte[] getImage(String filename) {
        try {
            return Files.readAllBytes(new File(productImagesPath + filename).toPath());
        } catch (IOException e) {
            throw new InvalidRequestException("Invalid filename");
        }
    }

    public void updateProduct(int productID, ProductRequestDTO request) {
        if (!productRepo.existsById(productID)) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepo.save(new Product(productID, request.getName(), request.getUnitPrice(), request.getLargeQty()
                , request.getMediumQty(), request.getSmallQty(), request.getCategory(), productID + ".png")
        );
    }

    public void updateProductImage(int productID, MultipartFile image) throws IOException {
        if (!productRepo.existsById(productID)) {
            throw new ResourceNotFoundException("Product not found");
        }
        image.transferTo(new File(productImagesPath + productID + ".png"));
    }

    public void deleteProduct(int productID) {
        Product product = productRepo.findById(productID)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepo.deleteById(productID);
        new File(productImagesPath + product.getImgFileName()).delete();
    }

    public List<Product> getProducts(List<Integer> productIdList) {
        return productRepo.findAllByProductIDIn(productIdList);
    }
}
