package com.example.productbackendform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductForm {
    private Long id;
    private String name;
    private int quantity;
    private int price;
    private String description;
    private MultipartFile image;
    private Category category;
}
