package com.mwibutsa.store.mappers;

import com.mwibutsa.store.dto.CartDto;
import com.mwibutsa.store.dto.CartItemDto;
import com.mwibutsa.store.entities.Cart;
import com.mwibutsa.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
