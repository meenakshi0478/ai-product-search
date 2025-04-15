package com.ai.productsearch.repository;

import com.ai.productsearch.model.Product;
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
    List<Product> searchProducts(@Param("query") String query);

    List<Product> findByCategory(String category);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findLatestProducts();

    @Query("SELECT p FROM Product p WHERE p.category = :category ORDER BY p.createdAt DESC")
    List<Product> findLatestProductsByCategory(@Param("category") String category);

    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.category = :category AND p.brand = :brand AND p.price = :price")
    List<Product> findByNameAndCategoryAndBrand(
        @Param("name") String name,
        @Param("category") String category,
        @Param("brand") String brand,
        @Param("price") BigDecimal price
    );
} 