package works.brm.catalog.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import works.brm.catalog.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
