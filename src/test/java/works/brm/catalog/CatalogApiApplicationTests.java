package works.brm.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CatalogApiApplicationTests {

    @Test
    void contextLoads() {
        // smoke test: application context starts with H2 + Flyway migrations
    }
}
