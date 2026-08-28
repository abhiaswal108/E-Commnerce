package com.aa.ecommerce.service;

import com.aa.ecommerce.dto.ProductRequestDTO;
import com.aa.ecommerce.dto.ProductResponseDTO;
import com.aa.ecommerce.entity.Category;
import com.aa.ecommerce.entity.Product;
import com.aa.ecommerce.exception.ResourceNotFoundException;
import com.aa.ecommerce.repository.CategoryRepository;
import com.aa.ecommerce.repository.ProductRepository;
import com.aa.ecommerce.specification.ProductSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    public ProductResponseDTO createProduct(ProductRequestDTO prod){
        Category category = categoryRepository.findById(prod.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + prod.getCategoryId()));
        Product product = new Product();
        product.setName(prod.getName());
        product.setDescription(prod.getDescription());
        product.setPrice(prod.getPrice());
        product.setStockQuantity(prod.getStockQuantity());
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);


    }
    private ProductResponseDTO mapToResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
    public ProductResponseDTO findProduct(Long id){
        Product p=productRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        ProductResponseDTO product=mapToResponseDTO(p);
        return product;

    }
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Page<Product> p=productRepository.findAll(pageable);
        Page<ProductResponseDTO> dto=p.map(this::mapToResponseDTO);
        return dto;
    }
    public Page<ProductResponseDTO> searchProducts(String name, Long categoryId,    // ← new method, add here
                                                   BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasName(name))
                .and(ProductSpecifications.hasCategoryId(categoryId))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice));

        Page<Product> results = productRepository.findAll(spec, pageable);
        Page<ProductResponseDTO> dto=results .map(this::mapToResponseDTO);
        return dto;
    }
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product p=productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        Category c=categoryRepository.findById(requestDTO.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category not found with id: " + requestDTO.getCategoryId()));

        p.setName(requestDTO.getName());
        p.setDescription(requestDTO.getDescription());
        p.setPrice(requestDTO.getPrice());
        p.setStockQuantity(requestDTO.getStockQuantity());
        p.setCategory(c);
        Product updatedProduct = productRepository.save(p);
        ProductResponseDTO dto = mapToResponseDTO(updatedProduct);
        return dto;


    }
    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(p);
    }
}
