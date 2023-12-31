package com.clothifystore.dto.request;

import com.clothifystore.enums.ProductCategories;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductRequestDTO {
    private String name;
    private double unitPrice;
    private int largeQty;
    private int mediumQty;
    private int smallQty;
    private ProductCategories category;
    private MultipartFile image;
}
