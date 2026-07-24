package com.mwibutsa.store.controllers;

import com.mwibutsa.store.dto.CreateProductRequest;
import com.mwibutsa.store.dto.ProductDto;
import com.mwibutsa.store.dto.UpdateProductRequest;
import com.mwibutsa.store.entities.Category;
import com.mwibutsa.store.entities.Product;
import com.mwibutsa.store.mappers.ProductMapper;
import com.mwibutsa.store.repositories.CategoryRepository;
import com.mwibutsa.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@RequestMapping("/products")
@RestController
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

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

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequest payload, UriComponentsBuilder uriBuilder) {
        Category category = categoryRepository.findById(payload.getCategoryId()).orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest()
                    .build();
        }
        Product newProduct = productMapper.toEntity(payload);
        newProduct.setCategory(category);
        productRepository.save(newProduct);

        ProductDto productDto = productMapper.toDto(newProduct);
        URI uri = uriBuilder.path("/products/{id}").buildAndExpand(productDto.getId()).toUri();

        return ResponseEntity.created(uri).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @RequestBody UpdateProductRequest payload,
            @PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        Category category;
        // category changes.
        if (!Objects.equals(payload.getCategoryId(), product.getCategory().getId())) {
            category = categoryRepository.findById(payload.getCategoryId()).orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().build();
            }
            product.setCategory(category);
        }

        productMapper.updateProduct(payload, product);
        productRepository.save(product);

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        productRepository.delete(product);
        
        return ResponseEntity.noContent().build();
    }
}
