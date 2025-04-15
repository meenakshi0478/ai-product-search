package com.ai.productsearch.repository;

import com.ai.productsearch.model.ProductVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVectorRepository extends JpaRepository<ProductVector, Long> {
    @Query(value = """
            SELECT pv.product_id 
            FROM product_vectors pv 
            WHERE pv.embedding <=> :queryVector::vector 
            ORDER BY pv.embedding <=> :queryVector::vector 
            LIMIT :limit
            """, nativeQuery = true)
    List<Long> findSimilarProducts(@Param("queryVector") String queryVector, @Param("limit") int limit);
    
    @Modifying
    @Query("DELETE FROM ProductVector pv WHERE pv.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

} 