package com.mwibutsa.store.controllers;

import com.mwibutsa.store.dto.ProductDto;
import com.mwibutsa.store.entities.Product;
import com.mwibutsa.store.mappers.ProductMapper;
import com.mwibutsa.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/products")
@RestController
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestParam(required = false, defaultValue = "", name = "categoryId") Byte categoryId) {
        
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId).stream().map(productMapper::toDto).toList();
        }
        return productRepository.findWithCategory().stream().map(productMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product));

    }
}
