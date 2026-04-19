package io.github.alburma.catalog.web.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public class ProductDtos {

    public record ProductRequest(
            @NotBlank @Size(max = 64) String sku,
            @NotBlank @Size(max = 255) String name,
            @Size(max = 4000) String description,
            @NotNull @PositiveOrZero Long priceCents,
            @NotBlank @Size(min = 3, max = 3) String currency,
            @NotNull @PositiveOrZero Integer stock,
            Long categoryId,
            Boolean active
    ) {}

    public record ProductUpdateRequest(
            @Size(max = 255) String name,
            @Size(max = 4000) String description,
            @PositiveOrZero Long priceCents,
            @Size(min = 3, max = 3) String currency,
            @PositiveOrZero Integer stock,
            Long categoryId,
            Boolean active
    ) {}

    public record ProductResponse(
            Long id,
            String sku,
            String name,
            String description,
            Long priceCents,
            String currency,
            Integer stock,
            Long categoryId,
            String categoryName,
            Boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
