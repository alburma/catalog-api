package works.brm.catalog.web;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> index() {
        return Map.of(
            "service", "catalog-api",
            "version", "0.1.0",
            "docs", "/swagger-ui.html",
            "openapi", "/v3/api-docs",
            "health", "/actuator/health"
        );
    }
}
