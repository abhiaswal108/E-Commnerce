package com.aa.ecommerce.controller;


import com.aa.ecommerce.dto.ProductRequestDTO;
import com.aa.ecommerce.dto.ProductResponseDTO;

import com.aa.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
        private final ProductService productService;

        @PostMapping
        public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO product) {
            ProductResponseDTO p = productService.createProduct(product);
            return new ResponseEntity<>(p, HttpStatus.CREATED);
        }
        @GetMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
            ProductResponseDTO p=productService.findProduct(id);
            return new ResponseEntity<>(p,HttpStatus.OK);
        }
        @GetMapping
        public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
                @PageableDefault(size = 10, sort = "name") Pageable pageable) {
                Page<ProductResponseDTO> p=productService.getAllProducts(pageable);
                return new ResponseEntity<>(p,HttpStatus.OK);
        }
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        Page<ProductResponseDTO> results = productService.searchProducts(name, categoryId, minPrice, maxPrice, pageable);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO p) {
        ProductResponseDTO pro = productService.updateProduct(id, p);
        return new ResponseEntity<>(pro, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
