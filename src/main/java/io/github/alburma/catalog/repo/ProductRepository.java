package io.github.alburma.catalog.repo;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.github.alburma.catalog.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("""
           SELECT p FROM Product p
           WHERE (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                              OR LOWER(p.sku)  LIKE LOWER(CONCAT('%', :q, '%')))
             AND (:categoryId IS NULL OR p.category.id = :categoryId)
             AND (:activeOnly = FALSE OR p.active = TRUE)
           """)
    Page<Product> search(@Param("q") String q,
                         @Param("categoryId") Long categoryId,
                         @Param("activeOnly") boolean activeOnly,
                         Pageable pageable);
}
