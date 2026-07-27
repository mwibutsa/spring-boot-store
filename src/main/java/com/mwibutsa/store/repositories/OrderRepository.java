package com.mwibutsa.store.repositories;

import com.mwibutsa.store.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}