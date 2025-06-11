package com.packhub.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateProductDTO {
    private String name;
    private Double price;
    private String userCode;
}
