package com.ai.productsearch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Arrays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_vectors")
public class ProductVector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false, columnDefinition = "vector")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private float[] embedding;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVector that = (ProductVector) o;
        return Arrays.equals(embedding, that.embedding);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(embedding);
    }
} 