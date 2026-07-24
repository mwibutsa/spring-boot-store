package com.mwibutsa.store.mappers;

import com.mwibutsa.store.dto.CreateProductRequest;
import com.mwibutsa.store.dto.ProductDto;
import com.mwibutsa.store.dto.UpdateProductRequest;
import com.mwibutsa.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);

    Product toEntity(CreateProductRequest productRequest);
    
    public void updateProduct(UpdateProductRequest updateRequest, @MappingTarget Product product);

}
