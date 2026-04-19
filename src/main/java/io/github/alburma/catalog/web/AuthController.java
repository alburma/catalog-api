package io.github.alburma.catalog.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.github.alburma.catalog.domain.User;
import io.github.alburma.catalog.exception.ApiException;
import io.github.alburma.catalog.repo.UserRepository;
import io.github.alburma.catalog.security.JwtService;
import io.github.alburma.catalog.web.dto.AuthDtos.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Register new user (default role USER)")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByUsername(req.username())) throw ApiException.conflict("Username taken");
        if (users.existsByEmail(req.email()))       throw ApiException.conflict("Email taken");
        User u = User.builder()
            .username(req.username())
            .email(req.email())
            .passwordHash(encoder.encode(req.password()))
            .role(User.Role.USER)
            .enabled(true)
            .build();
        users.save(u);
        String token = jwtService.generate(u.getUsername(), u.getRole().name());
        return ResponseEntity.status(201).body(new TokenResponse(token, "Bearer", jwtService.getExpirationSeconds()));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username+password, returns JWT")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User u = users.findByUsername(auth.getName())
            .orElseThrow(() -> ApiException.notFound("User"));
        String token = jwtService.generate(u.getUsername(), u.getRole().name());
        return new TokenResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }

    @GetMapping("/me")
    @Operation(summary = "Current user info (requires Bearer token)")
    public MeResponse me(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal.toString())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Not authenticated");
        }
        String username = principal.toString();
        User u = users.findByUsername(username).orElseThrow(() -> ApiException.notFound("User"));
        return new MeResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name());
    }
}
