package com.mwibutsa.store.mappers;

import com.mwibutsa.store.dto.ProductDto;
import com.mwibutsa.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);
}
