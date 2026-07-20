package com.clothifystore.dto.response;

import com.clothifystore.enums.ProductSizes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailResponseDTO {
    private int id;
    private int productID;
    private ProductSizes size;
    private int quantity;
    private double unitPrice;
}
