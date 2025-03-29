package com.fpt.shopapp.repositories;

import com.fpt.shopapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable); //phân trang

    @Query("select p from Product p where "+
    "(:categoryId is null or :categoryId = 0 or p.category.id = :categoryId) "+
    "and (:keyword is null or :keyword = '' or p.name like %:keyword% or p.description like %:keyword%)")
    Page<Product> searchProducts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);

    @Query("select p from Product p left join fetch p.productImages where p.id = :productId")
    Optional<Product> getDetailProduct(@Param("productId") Long productId);

    @Query("select p from Product p where p.id in :productIds")
    List<Product> findProductsByIds(@Param("productIds") List<Long> productIds);
}
