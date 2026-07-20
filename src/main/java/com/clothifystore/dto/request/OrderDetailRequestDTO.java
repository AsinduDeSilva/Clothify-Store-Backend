package com.clothifystore.dto.request;

import com.clothifystore.enums.ProductSizes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailRequestDTO {
    private int productID;
    private ProductSizes size;
    private int quantity;
    private double unitPrice;
}
