package com.mwibutsa.store.controllers;

import com.mwibutsa.store.dto.AddItemToCartRequest;
import com.mwibutsa.store.dto.CartDto;
import com.mwibutsa.store.dto.CartItemDto;
import com.mwibutsa.store.dto.UpdateCartItemRequest;
import com.mwibutsa.store.entities.Cart;
import com.mwibutsa.store.entities.CartItem;
import com.mwibutsa.store.mappers.CartMapper;
import com.mwibutsa.store.repositories.CartRepository;
import com.mwibutsa.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ) {
        var cart = new Cart();
        cartRepository.save(cart);
        var cartDto = cartMapper.toDto(cart);
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();

        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addProduct(@PathVariable UUID cartId, @RequestBody AddItemToCartRequest payload) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        var product = productRepository.findById(payload.getProductId()).orElse(null);

        if (product == null) {
            return ResponseEntity.badRequest().build();
        }

        var cartItem = cart.getItems().stream()
                .filter((var item) -> Objects.equals(item.getProduct().getId(), product.getId()))
                .findFirst().orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(cart);
        }
        cart.getItems().add(cartItem);
        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);

    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        var cartDto = cartMapper.toDto(cart);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> updateCartItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @RequestBody UpdateCartItemRequest payload) {

        var cart = cartRepository.getCartWithSpecificItem(cartId, productId).orElse(null);

        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        var cartItem = cart.getItems().stream().findFirst().orElse(null);

        if (cartItem == null) {
            return ResponseEntity.badRequest().build();
        } else {
            cartItem.setQuantity(payload.getQuantity());
        }

        return ResponseEntity.noContent().build();

    }
}
