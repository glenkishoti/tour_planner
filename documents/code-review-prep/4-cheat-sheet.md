# Defense Cheat Sheet — TourPlanner Backend

One page to glance at during the review.

## Stack (one breath)
Spring Boot 3.2.5 · Java 21 · Spring Web · Spring Security + JWT (jjwt) · Spring Data JPA / Hibernate · PostgreSQL · Lombok · Log4j2 · Apache HttpClient 5 · Maven · Docker.

## Layers (point downward only)
**Controller** (HTTP, `@Valid`, DTOs) → **Service** (`@Transactional` business logic, ownership) → **Repository** (Spring Data JPA) → **PostgreSQL**.
Side: **OpenRouteServiceClient** (external routing) · **Security** (JWT filter) · **DTO/mapping**.

## Auth flow (5 beats)
1. Login → BCrypt verify → `JwtService` mints signed token (24 h).
2. Client sends `Authorization: Bearer <token>`.
3. `JwtAuthenticationFilter` (OncePerRequest) validates + sets `SecurityContext`.
4. `SecurityConfig`: `/api/auth/**` public, rest authenticated, STATELESS, CSRF off.
5. Services read user from context → enforce ownership.

## Endpoints
`/api/auth` (register, login) · `/api/tours` (CRUD + image) · `/api/tours/{id}/logs` (CRUD) · `/api/search` (tours, tour-logs, advanced) · `/api/statistics` · `/api/import-export`.

## Data model
`User 1—* Tour 1—* TourLog`. Tour→logs: lazy, cascade ALL, orphanRemoval. `Duration` stored as minutes via custom `AttributeConverter`. Computed `@Transient`: **popularity** (= #logs), **child-friendliness** (weighted 0–100).

## Patterns to name
Repository · DTO · Builder · Dependency Injection · Adapter/Gateway (ORS client) · transient computed domain logic.

## Why these choices (quick answers)
- **DTOs vs entities** → decouple API from schema, hide `password`.
- **Constructor injection** → explicit, final, testable.
- **CSRF off** → bearer token in header, not cookies → no CSRF vector.
- **Ownership via `...AndUserId`** → blocks IDOR; wrong-user → 404.
- **BCrypt** → salted, adaptive password hashing.
- **LAZY fetch** → avoid loading full graph; safe inside `@Transactional`.
- **Custom converter** → JPA has no native `Duration` mapping.

## Testing
66 unit tests. Services with mocked repos · repositories on H2 · JWT · auth controller · DTO validation. Gap: integration (`@SpringBootTest` + MockMvc) — would add.

## Know-your-flaws (say before they do)
1. **Committed JWT secret / DB password defaults** → externalize, drop the fallback. *(.env is gitignored.)*
2. **No `@Valid` on registration** → add `@NotBlank/@Size/@Email`.
3. **Duplicate username → 500** (raw `RuntimeException`) → should be **409** via global handler.
4. **No global exception handler** → add one `@RestControllerAdvice`.
5. **Lombok `@Data` on entities** → recursion in toString + password in logs; use `@Getter/@Setter`, exclude relations.
6. **ORS: new HttpClient per call + possible NPE on empty 200** → reuse client, return fallback.
7. **File upload**: svg→XSS, unchecked fallback path read → whitelist types, remove fallback.

## Top 3 improvements (if asked)
1. Single global `@RestControllerAdvice` (consistent status codes, 409 for duplicates).
2. Validation + remove committed secret defaults.
3. Centralize entity↔DTO mapping; extract duplicated `getCurrentUser()`.

## What's genuinely strong (lead with these)
Clean layering · constructor injection everywhere · ownership enforced at the DB (no IDOR) · BCrypt + stateless JWT · graceful ORS fallback · DTOs hide internals · 66 tests · tidy `Duration` converter.
