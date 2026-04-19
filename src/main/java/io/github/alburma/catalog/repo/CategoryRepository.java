package io.github.alburma.catalog.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github.alburma.catalog.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
}
