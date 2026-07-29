package com.mwibutsa.store.mappers;

import com.mwibutsa.store.dto.OrderDto;
import com.mwibutsa.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
