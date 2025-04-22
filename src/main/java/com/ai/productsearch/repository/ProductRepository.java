package com.ai.productsearch.repository;

import com.ai.productsearch.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);

    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    Page<Product> findLatestProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category = :category ORDER BY p.createdAt DESC")
    Page<Product> findLatestProductsByCategory(@Param("category") String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.category = :category AND p.brand = :brand AND p.price = :price")
    List<Product> findByNameAndCategoryAndBrand(
        @Param("name") String name,
        @Param("category") String category,
        @Param("brand") String brand,
        @Param("price") BigDecimal price
    );

    boolean existsByUpc(String upc);

    @Query("SELECT p FROM Product p WHERE p.upc = :upc ORDER BY p.createdAt ASC")
    List<Product> findByUpc(@Param("upc") String upc);
} 