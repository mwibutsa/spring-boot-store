package com.mwibutsa.store.repositories;

import com.mwibutsa.store.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("select c from Cart c")
    @EntityGraph(attributePaths = "items.product")
    Optional<Cart> getCartWithItems(UUID cartId);


    @Query("select c from Cart c join c.items i where c.id = :cartId and i.product.id = :productId")
    @EntityGraph(attributePaths = "items.product")
    Optional<Cart> getCartWithSpecificItem(UUID cartId, Long productId);
}