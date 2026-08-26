package com.example.zorashopminishopee.module.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "inventory")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false, unique = true)
    private ProductVariant variant;

    @OneToMany(mappedBy = "inventory",  fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InventoryLog> inventoryLog;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer reserved = 0;

    @Builder.Default
    @Column(name = "available", insertable = false, updatable = false)
    private Integer available = 0;

    @Builder.Default
    @Version
    @Column(nullable = false)
    private Integer version = 0;
}
