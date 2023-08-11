package com.clothifystore.controller;

import com.clothifystore.dto.request.ProductRequestDTO;
import com.clothifystore.dto.response.CrudResponse;
import com.clothifystore.entity.Product;
import com.clothifystore.enums.ProductCategories;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Value("${product.images.path}")
    private String productImagesPath;

    @Autowired
    private ProductRepo productRepo;

    private final String productNotFound = "Product not found";

    @PostMapping
    public ResponseEntity<CrudResponse> addProduct(@ModelAttribute ProductRequestDTO request) throws IOException {

        int productID = productRepo.save(new Product()).getProductID();
        request.getImage().transferTo(new File(productImagesPath + productID + ".png"));

        productRepo.save(new Product(productID, request.getName(), request.getUnitPrice(), request.getLargeQty()
                , request.getMediumQty(), request.getSmallQty(), request.getCategory(), productID+".png")
        );

        return ResponseEntity.ok(new CrudResponse(true, "Product Added"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable (value = "id")int productID){

        Optional<Product> productOptional = productRepo.findById(productID);
        if(productOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, productNotFound));
        }
        return ResponseEntity.ok(productOptional.get());
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<?> getAllOrdersByPage(@PathVariable(value = "page")int page){

        Sort sort = Sort.by(Sort.Order.desc("productID"));
        Pageable pageable = PageRequest.of(page - 1, 24, sort);
        return ResponseEntity.ok(productRepo.findAll(pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(value = "category") String category,
                                                   @RequestParam(value = "page") int page){

        Sort sort = Sort.by(Sort.Order.desc("productID"));
        Pageable pageable = PageRequest.of(page - 1, 24, sort);
        switch (category){
            case "men":
                return ResponseEntity.ok(productRepo.findAllByCategory(ProductCategories.MEN, pageable));
            case "women":
                return ResponseEntity.ok(productRepo.findAllByCategory(ProductCategories.WOMEN, pageable));
            case "kids":
                return ResponseEntity.ok(productRepo.findAllByCategory(ProductCategories.KIDS, pageable));
            case "accessories":
                return ResponseEntity.ok(productRepo.findAllByCategory(ProductCategories.ACCESSORIES, pageable));
            default:
                return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid category"));
        }
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<?> getImage(@PathVariable(value = "filename")String filename) {

        try {
            byte[] image = Files.readAllBytes(new File(productImagesPath+filename).toPath());
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(image);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new CrudResponse(false, "Invalid filename"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrudResponse> updateProduct(@PathVariable(value = "id")int productID,
                                                      @ModelAttribute ProductRequestDTO request) throws IOException {

        Optional<Product> productOptional = productRepo.findById(productID);
        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new CrudResponse(false,  productNotFound));
        }

        productRepo.save(new Product(productID, request.getName(), request.getUnitPrice(), request.getLargeQty()
                , request.getMediumQty(), request.getSmallQty(), request.getCategory(), productID + ".png")
        );

        return ResponseEntity.ok(new CrudResponse(true, "Product Updated"));
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<CrudResponse> updateProductImage(@PathVariable(value = "id") int productID,
                                                           @RequestParam(value = "image") MultipartFile image) throws IOException {

        if (!productRepo.existsById(productID)) {
            return ResponseEntity.badRequest().body(new CrudResponse(false,  productNotFound));
        }

        image.transferTo(new File(productImagesPath + productID + ".png"));
        return ResponseEntity.ok(new CrudResponse(true, "Product Image Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudResponse> deleteProduct(@PathVariable(value = "id")int productID){

        Optional<Product> productOptional = productRepo.findById(productID);
        if(productOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CrudResponse(false, productNotFound));
        }
        productRepo.deleteById(productID);
        new File(productImagesPath + productOptional.get().getImgFileName()).delete();
        return ResponseEntity.ok(new CrudResponse(true, "Product Deleted"));
    }

}
