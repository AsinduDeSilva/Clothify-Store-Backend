package com.clothifystore.controller;

import com.clothifystore.dto.request.ProductRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Product;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final String PRODUCT_IMAGES_FOLDER_PATH = "C:\\Asindu\\Clothify Store\\Product Images\\";

    @Autowired
    private ProductRepo productRepo;

    @PostMapping
    public ResponseEntity<CrudResponse> addProduct(@ModelAttribute ProductRequestDTO request) throws IOException {
        request.getImage().transferTo(new File(PRODUCT_IMAGES_FOLDER_PATH+request.getImage().getOriginalFilename()));

        productRepo.save(new Product(0, request.getName(), request.getUnitPrice(), request.getLargeQty()
                , request.getMediumQty(), request.getSmallQty(), request.getCategory(), request.getImage().getOriginalFilename())
        );

        return ResponseEntity.ok(new CrudResponse(true, "Product Added"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable (value = "id")int productID){
        if(productRepo.findById(productID).isPresent()){
            return ResponseEntity.ok(productRepo.findById(productID).get());
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Product not found"));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(productRepo.findAll());
    }

    @GetMapping("/men")
    public ResponseEntity<List<Product>> getAllMenProducts(){
        return ResponseEntity.ok(productRepo.findByCategory("Men"));
    }

    @GetMapping("/women")
    public ResponseEntity<List<Product>> getAllWomenProducts(){
        return ResponseEntity.ok(productRepo.findByCategory("Women"));
    }

    @GetMapping("/kids")
    public ResponseEntity<List<Product>> getAllKidsProducts(){
        return ResponseEntity.ok(productRepo.findByCategory("Kids"));
    }

    @GetMapping("/accessories")
    public ResponseEntity<List<Product>> getAllAccessories(){
        return ResponseEntity.ok(productRepo.findByCategory("Accessories"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateProduct(@PathVariable(value = "id")int produtID,
                                                      @ModelAttribute ProductRequestDTO request) throws IOException {

        if (productRepo.findById(produtID).isPresent()) {

            new File(PRODUCT_IMAGES_FOLDER_PATH + productRepo.findById(produtID).get().getImgFileName()).delete();
            request.getImage().transferTo(new File(PRODUCT_IMAGES_FOLDER_PATH + request.getImage().getOriginalFilename()));

            productRepo.save(new Product(produtID, request.getName(), request.getUnitPrice(), request.getLargeQty()
                    , request.getMediumQty(), request.getSmallQty(), request.getCategory(), request.getImage().getOriginalFilename())
            );

            return ResponseEntity.ok(new CrudResponse(true, "Product Updated"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false, "Product not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudResponse> deleteProduct(@PathVariable(value = "id")int productID){
        if(productRepo.existsById(productID)){
            productRepo.deleteById(productID);
            return ResponseEntity.ok(new CrudResponse(true, "Product Deleted"));
        }
        return ResponseEntity.badRequest().body(new CrudResponse(false,"Product not found"));
    }
}
