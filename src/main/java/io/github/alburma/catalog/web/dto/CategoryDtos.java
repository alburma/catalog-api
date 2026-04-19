package io.github.alburma.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDtos {

    public record CategoryRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 140) String slug
    ) {}

    public record CategoryResponse(Long id, String name, String slug) {}
}
