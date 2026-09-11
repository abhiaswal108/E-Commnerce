package com.aa.ecommerce.test;

import com.aa.ecommerce.dto.ProductRequestDTO;
import com.aa.ecommerce.dto.ProductResponseDTO;
import com.aa.ecommerce.entity.Category;
import com.aa.ecommerce.entity.Product;
import com.aa.ecommerce.exception.ResourceNotFoundException;
import com.aa.ecommerce.repository.CategoryRepository;
import com.aa.ecommerce.repository.ProductRepository;
import com.aa.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldSaveAndReturnProduct_whenCategoryExists() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setName("Test Mouse");
        requestDTO.setPrice(BigDecimal.valueOf(25.99));
        requestDTO.setStockQuantity(10);
        requestDTO.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Mouse");
        savedProduct.setPrice(BigDecimal.valueOf(25.99));
        savedProduct.setStockQuantity(10);
        savedProduct.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ProductResponseDTO result = productService.createProduct(requestDTO);

        // Assert
        assertEquals("Test Mouse", result.getName());
        assertEquals("Electronics", result.getCategoryName());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    @Test
    void createProduct_shouldThrowException_whenCategoryNotFound() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setCategoryId(999L);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(requestDTO);
        });

        verify(productRepository, never()).save(any(Product.class));
    }
    @Test
    void updateProduct_shouldUpdateAndReturnProduct_whenProductAndCategoryExist() {
        // Arrange — think through what you need to mock here
        // 1. An existing Product (as if fetched from the DB)
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Name");
        existingProduct.setPrice(BigDecimal.valueOf(10.00));
        existingProduct.setStockQuantity(5);
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setName("Test Key");
        requestDTO.setPrice(BigDecimal.valueOf(15.99));
        requestDTO.setStockQuantity(10);
        requestDTO.setCategoryId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        ProductResponseDTO result = productService.updateProduct(1L, requestDTO);
        assertEquals("Test Key", result.getName());
        assertEquals(BigDecimal.valueOf(15.99), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

}