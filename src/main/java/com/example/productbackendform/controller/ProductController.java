package com.example.productbackendform.controller;

import com.example.productbackendform.model.Category;
import com.example.productbackendform.model.Product;
import com.example.productbackendform.model.ProductForm;
import com.example.productbackendform.service.category.ICategoryService;
import com.example.productbackendform.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Value("${file-upload}")
    String uploadPath;


    @GetMapping
    public ResponseEntity<Page<Product>> findAllProduct(@PageableDefault(value = 50) Pageable pageable) {
        Page<Product> products = productService.findAll(pageable);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalProduct.get(), HttpStatus.OK);
    }
//
//    @PostMapping
//    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
//        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
//    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@ModelAttribute ProductForm productForm) {
        String fileName = productForm.getImage().getOriginalFilename();
        Long currenTime = System.currentTimeMillis();
        fileName =currenTime + fileName;
        try {
            FileCopyUtils.copy(productForm.getImage().getBytes(), new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getName(), productForm.getQuantity(), productForm.getPrice(), productForm.getDescription(), fileName, productForm.getCategory());
//        product.setCategory(productForm.getCategory());
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }



//    @PutMapping("/{id}")
//    public ResponseEntity<Product> editProduct(@PathVariable Long id, @RequestBody Product product) {
//        Optional<Product> optionalProduct = productService.findById(id);
//        if (!optionalProduct.isPresent()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        product.setId(optionalProduct.get().getId());
//        return new ResponseEntity<>(productService.save(product), HttpStatus.OK);
//    }


    @PostMapping("/{id}")
    private ResponseEntity<Product> editProduct(@PathVariable Long id, ProductForm productForm) {
        Optional<Product> productOptional = productService.findById(id);

        if (!productOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();
        MultipartFile multipartFile = productForm.getImage();
        if (multipartFile != null && multipartFile.getSize() != 0) {
            File file = new File(uploadPath + product.getImage());
            if (file.exists()) {
                file.delete();
            }
            String fileName = productForm.getImage().getOriginalFilename();
            Long currenTime = System.currentTimeMillis();
            fileName = currenTime + fileName;
            product.setImage(fileName);
            try {
                FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());
        product.setDescription(productForm.getDescription());
        product.setCategory(productForm.getCategory());
        productService.save(product);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


//    @DeleteMapping("/{id}")
//    public ResponseEntity<Product> deleteProductById(@PathVariable Long id) {
//        Optional<Product> optionalProduct = productService.findById(id);
//        if (!optionalProduct.isPresent()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        productService.removeById(id);
//        return new ResponseEntity<>(optionalProduct.get(), HttpStatus.OK);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        File file = new File(uploadPath + productOptional.get().getImage());
        if (file.exists()) {
            file.delete();
        }
        productService.removeById(id);
        return new ResponseEntity<>(productOptional.get(), HttpStatus.OK);
    }
}