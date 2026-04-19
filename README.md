# Catalog API — Spring Boot 3 + Java 21

Production-style REST API for a product catalog. Built as a portfolio sample demonstrating a clean, layered Spring Boot backend: JPA domain model, JWT-secured endpoints, Flyway migrations, validated DTOs, OpenAPI docs, MockMvc integration tests, Docker, and GitHub Actions CI.

- **Stack:** Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA, Flyway, PostgreSQL (prod) / H2 (dev+test), JJWT, springdoc-openapi, Lombok, JUnit 5 + MockMvc
- **Build:** Maven (wrapper included)
- **Run:** single JAR, or `docker compose up`
- **CI:** GitHub Actions — build + tests on every push

---

## Quickstart

### Run with H2 (no DB setup — 1 command)
```bash
./mvnw spring-boot:run
```
App starts on `http://localhost:8080` with the `dev` profile. Flyway creates the schema and seeds demo data.

### Run with PostgreSQL via Docker Compose
```bash
docker compose up --build
```

### Explore
- Swagger UI:   <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Health:       <http://localhost:8080/actuator/health>
- H2 console (dev only): <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:catalog`, user `sa`)

---

## Demo accounts (seeded)

| Username | Password      | Role   |
| -------- | ------------- | ------ |
| `admin`  | `password123` | ADMIN  |
| `user`   | `password123` | USER   |

---

## API tour (curl)

```bash
# 1) Public: list products
curl -s http://localhost:8080/api/products | jq '.content[0:2]'

# 2) Public: search
curl -s "http://localhost:8080/api/products?q=hoodie&size=5" | jq

# 3) Login → get JWT
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"password123"}' | jq -r .token)

# 4) Authenticated: who am I
curl -s http://localhost:8080/api/auth/me -H "Authorization: Bearer $TOKEN" | jq

# 5) ADMIN-only: create a product
curl -s -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sku":"DEMO-1","name":"Demo","priceCents":1999,"currency":"EUR","stock":10,"categoryId":1}' | jq

# 6) USER token is rejected with 403 for admin endpoints
USER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"user","password":"password123"}' | jq -r .token)
curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sku":"X","name":"x","priceCents":1,"currency":"EUR","stock":1}'
# → 403
```

---

## Endpoints

### Auth
| Method | Path                   | Auth  | Description                        |
| ------ | ---------------------- | ----- | ---------------------------------- |
| POST   | `/api/auth/register`   | —     | Register, returns JWT              |
| POST   | `/api/auth/login`      | —     | Login, returns JWT                 |
| GET    | `/api/auth/me`         | JWT   | Current user profile               |

### Products
| Method | Path                    | Auth   | Description                                       |
| ------ | ----------------------- | ------ | ------------------------------------------------- |
| GET    | `/api/products`         | —      | Paged list: `?q=&categoryId=&activeOnly=&page=&size=&sort=` |
| GET    | `/api/products/{id}`    | —      | Get one                                           |
| POST   | `/api/products`         | ADMIN  | Create                                            |
| PATCH  | `/api/products/{id}`    | ADMIN  | Partial update                                    |
| DELETE | `/api/products/{id}`    | ADMIN  | Delete                                            |

### Categories
| Method | Path                      | Auth   | Description    |
| ------ | ------------------------- | ------ | -------------- |
| GET    | `/api/categories`         | —      | List all       |
| POST   | `/api/categories`         | ADMIN  | Create         |
| DELETE | `/api/categories/{id}`    | ADMIN  | Delete         |

All write endpoints validate with `jakarta.validation`; validation errors return a structured `ErrorResponse` with field-level details.

---

## Project layout

```
src/main/java/works/brm/catalog
├── domain/         JPA entities (Product, Category, User)
├── repo/           Spring Data repositories
├── service/        Business logic, transactional boundaries
├── web/            Controllers + DTOs
│   └── dto/
├── security/       JwtService, JwtAuthFilter, SecurityConfig, UserDetailsService
├── exception/      ApiException + GlobalExceptionHandler
└── config/         OpenApiConfig

src/main/resources
├── application.yml           (base + JWT settings)
├── application-dev.yml       (H2 in-memory, dev migration folder)
├── application-prod.yml      (PostgreSQL)
└── db/migration/V1__...sql   (Flyway)
└── db/dev/V900__...sql       (dev seed, dev profile only)
```

---

## Testing

```bash
./mvnw test
```

- `CatalogApiApplicationTests` — context-loads smoke test
- `AuthFlowTest` — register, login, `/me` with JWT
- `ProductApiIntegrationTest` — public listing, search, role-based access, JWT admin flow

All tests run against in-memory H2 with the same Flyway migrations as production — no Docker required in CI.

---

## Security model

- Stateless JWT (HS256, configurable secret + TTL)
- BCrypt password hashing
- Role-based authorization via `@PreAuthorize("hasRole('ADMIN')")` on write endpoints
- Structured auth failures (401 vs 403) via `GlobalExceptionHandler`

**Production checklist (when hosting):**
- [ ] Set `JWT_SECRET` to a 64+ byte random value (never commit)
- [ ] Put the API behind HTTPS
- [ ] Point `DATABASE_URL` / `DB_USER` / `DB_PASSWORD` at a managed PostgreSQL
- [ ] Disable H2 and the dev migration folder (they are dev-profile only)

---

## Environment variables

| Var                     | Default                                     | Notes                           |
| ----------------------- | ------------------------------------------- | ------------------------------- |
| `SPRING_PROFILES_ACTIVE`| `dev`                                       | `prod` for PostgreSQL           |
| `PORT`                  | `8080`                                      |                                 |
| `DATABASE_URL`          | `jdbc:postgresql://localhost:5432/catalog`  | prod only                       |
| `DB_USER`               | `catalog`                                   | prod only                       |
| `DB_PASSWORD`           | `catalog`                                   | prod only                       |
| `JWT_SECRET`            | dev default (insecure)                      | **must override in prod, ≥32B** |
| `JWT_EXP_MIN`           | `60`                                        | token TTL in minutes            |

---

## License

MIT
