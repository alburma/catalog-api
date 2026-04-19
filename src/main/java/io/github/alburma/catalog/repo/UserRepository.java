package io.github.alburma.catalog.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github.alburma.catalog.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
