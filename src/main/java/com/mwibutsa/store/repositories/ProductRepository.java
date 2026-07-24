package com.mwibutsa.store.repositories;

import com.mwibutsa.store.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("select p from Product p")
    @EntityGraph(attributePaths = {"category"})
    List<Product> findWithCategory();

    @Query("select p from Product p where p.category.id = :categoryId")
    @EntityGraph(attributePaths = {"category"})
    List<Product> findByCategoryId(@Param("categoryId") Byte categoryId);
}