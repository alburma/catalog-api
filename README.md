# catalog-api

Product catalog REST API. Spring Boot 3 backend with JWT-secured admin endpoints, Flyway-managed PostgreSQL schema, OpenAPI docs, and a containerised deploy. Built as a reference implementation of a production-shaped Spring Boot service.

## What it demonstrates

- Layered architecture: `domain → repo → service → web` with transactional boundaries and DTO-based contracts
- Stateless JWT authentication with role-based authorization (`@PreAuthorize`), BCrypt password hashing
- Versioned PostgreSQL schema via Flyway; dev profile uses H2 in PostgreSQL-compat mode with the same migrations
- Input validation (`jakarta.validation`) and a single `GlobalExceptionHandler` returning structured error responses
- Paged, filterable search (`q`, `categoryId`, `activeOnly`) via Spring Data `Pageable`
- OpenAPI 3 schema + Swagger UI with Bearer auth wired in
- Integration tests on MockMvc covering the full auth flow and role-based access control (9 tests, no Docker required)
- Multi-stage Dockerfile (layered JAR, non-root user) and `docker compose` for local PostgreSQL
- GitHub Actions CI: build + test on every push

## Tech stack

Java 21 · Spring Boot 3.5 · Spring Security · Spring Data JPA · Flyway · PostgreSQL · JJWT · springdoc-openapi · JUnit 5 · MockMvc · Maven · Docker

## Architecture

```
src/main/java/works/brm/catalog
├── domain/       JPA entities (Product, Category, User)
├── repo/         Spring Data repositories
├── service/      Transactional business logic
├── web/          REST controllers + request/response records
│   └── dto/
├── security/     JwtService, JwtAuthFilter, SecurityConfig, UserDetailsService
├── exception/    ApiException + GlobalExceptionHandler
└── config/       OpenAPI configuration
```

## API

| Method | Path                    | Auth   | Purpose                                         |
| ------ | ----------------------- | ------ | ----------------------------------------------- |
| POST   | `/api/auth/register`    | —      | Create account, returns JWT                     |
| POST   | `/api/auth/login`       | —      | Exchange credentials for JWT                    |
| GET    | `/api/auth/me`          | Bearer | Return current user profile                     |
| GET    | `/api/products`         | —      | Paged search: `q`, `categoryId`, `activeOnly`   |
| GET    | `/api/products/{id}`    | —      | Fetch a product                                 |
| POST   | `/api/products`         | ADMIN  | Create product                                  |
| PATCH  | `/api/products/{id}`    | ADMIN  | Partial update                                  |
| DELETE | `/api/products/{id}`    | ADMIN  | Delete                                          |
| GET    | `/api/categories`       | —      | List categories                                 |
| POST   | `/api/categories`       | ADMIN  | Create category                                 |
| DELETE | `/api/categories/{id}`  | ADMIN  | Delete category                                 |

Full schema: `GET /v3/api-docs` · Interactive docs: `GET /swagger-ui.html`

## Example

```bash
# authenticate
TOKEN=$(curl -s -X POST "$API_URL/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"username":"...","password":"..."}' | jq -r .token)

# paged search
curl -s "$API_URL/api/products?q=hoodie&size=10&sort=name,asc" \
  -H "Authorization: Bearer $TOKEN"

# admin-only write
curl -s -X POST "$API_URL/api/products" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sku":"SKU-001","name":"Name","priceCents":1999,"currency":"EUR","stock":10,"categoryId":1}'
```

Non-admin tokens receive `403 Forbidden` on write endpoints. Invalid or expired tokens return `401 Unauthorized` through the global exception handler.

## Build & test

```bash
./mvnw verify
```

Nine integration tests run against an in-memory H2 database with the same Flyway migrations used in production. No Docker required in CI.

## Run locally

```bash
# In-memory DB, single command
./mvnw spring-boot:run

# Or with a real PostgreSQL
docker compose up --build
```

## Configuration

| Variable                 | Purpose                            | Required in prod |
| ------------------------ | ---------------------------------- | ---------------- |
| `SPRING_PROFILES_ACTIVE` | `dev` or `prod`                    | yes (`prod`)     |
| `DATABASE_URL`           | JDBC URL                           | yes              |
| `DB_USER` / `DB_PASSWORD`| DB credentials                     | yes              |
| `JWT_SECRET`             | HS256 key, ≥32 bytes               | yes              |
| `JWT_EXP_MIN`            | Token TTL in minutes (default 60)  | no               |
| `PORT`                   | Server port (default 8080)         | no               |

The `dev` profile ships a small seed migration (`db/dev/V900__dev_seed.sql`) with sample products, categories, and two local-only accounts for exercising the auth flow. These are not loaded when `SPRING_PROFILES_ACTIVE=prod`.

## License

MIT
