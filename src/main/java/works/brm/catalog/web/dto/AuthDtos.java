package works.brm.catalog.web.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 80) String username,
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(min = 8, max = 100) String password
    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record TokenResponse(String token, String tokenType, long expiresInSeconds) {}

    public record MeResponse(Long id, String username, String email, String role) {}
}
