package com.mwibutsa.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();

}