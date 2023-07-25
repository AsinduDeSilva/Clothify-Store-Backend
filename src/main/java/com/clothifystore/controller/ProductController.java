package com.clothifystore.controller;

import com.clothifystore.dto.request.ProductRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Product;
import com.clothifystore.enums.ProductCategories;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(value = "category")String category){
        switch (category){
            case "men":
                return ResponseEntity.ok(productRepo.findByCategory(ProductCategories.MEN));
            case "women":
                return ResponseEntity.ok(productRepo.findByCategory(ProductCategories.WOMEN));
            case "kids":
                return ResponseEntity.ok(productRepo.findByCategory(ProductCategories.KIDS));
            case "accessories":
                return ResponseEntity.ok(productRepo.findByCategory(ProductCategories.ACCESSORIES));
            default:
                return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid category"));
        }
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<?> getImage(@PathVariable(value = "filename")String filename) {
        try {
            byte[] image = Files.readAllBytes(new File(PRODUCT_IMAGES_FOLDER_PATH+filename).toPath());
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(image);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid filename"));
        }
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
