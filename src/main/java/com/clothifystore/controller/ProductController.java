package com.clothifystore.controller;

import com.clothifystore.dto.request.ProductRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<CrudResponse> addProduct(@ModelAttribute ProductRequestDTO request) throws IOException {
        productService.addProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CrudResponse(true, "Product Added")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable(value = "id") int productID) {
        return ResponseEntity.ok(productService.getProduct(productID));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(value = "category") String category,
                                                   @RequestParam(value = "page") int page) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, page));
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<?> getImage(@PathVariable(value = "filename") String filename) {
        byte[] image = productService.getImage(filename);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateProduct(@PathVariable(value = "id") int productID,
                                                      @ModelAttribute ProductRequestDTO request) {
        productService.updateProduct(productID, request);
        return ResponseEntity.ok(new CrudResponse(true, "Product Updated"));
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<CrudResponse> updateProductImage(@PathVariable(value = "id") int productID,
                                                           @RequestParam(value = "image") MultipartFile image) throws IOException {
        productService.updateProductImage(productID, image);
        return ResponseEntity.ok(new CrudResponse(true, "Product Image Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudResponse> deleteProduct(@PathVariable(value = "id") int productID) {
        productService.deleteProduct(productID);
        return ResponseEntity.ok(new CrudResponse(true, "Product Deleted"));
    }

    @PostMapping("list")
    public ResponseEntity<?> getProducts(@RequestBody List<Integer> productIdList) {
        return ResponseEntity.ok(productService.getProducts(productIdList));
    }
}
