# Backend Code Audit — TourPlanner

Audit of `tourplanner-backend` (Spring Boot 3.2.5, Java 21) ahead of the code review. Findings are grouped by severity. Each one has **what**, **where**, **why it matters**, and a **fix**. For an oral defense, knowing these *before* the reviewer points them out is your strongest position — you show you understand your own trade-offs.

How to use this: skim the High and Medium tables. For each High item, either fix it before the review or be ready to say "I know about this, here's why it's like that and here's how I'd fix it." Reviewers reward awareness almost as much as a clean codebase.

---

## Summary

| Severity | Count | Theme |
|---|---|---|
| High | 5 | Secrets in config, missing input validation, Lombok `@Data` on JPA entities, file-upload safety, NPE in route lookup |
| Medium | 7 | Error handling consistency, duplicated code, CORS double-config, HTTP client per-call, import bypasses validation |
| Low | 6 | Info leakage in error messages, in-memory filtering, public fields, logging, ddl-auto |

The architecture itself is solid: clean layering (controller → service → repository), constructor injection throughout, DTOs separating API from entities, JWT stateless auth, and 66 unit tests. The findings below are refinements, not a rewrite.

---

## High severity

### H1 — Committed default JWT secret and DB password
**Where:** `application.properties`
```properties
jwt.secret=${JWT_SECRET:Y2hhbmdlbXlqd3RzZWNyZXRrZXlmb3Jwcm9kdWN0aW9udXNldXNl}
spring.datasource.password=${DB_PASSWORD:tourpass}
```
**Why it matters:** The `${VAR:default}` syntax means if the env var is missing, the app uses the committed fallback. That base64 string decodes to a known value, so anyone with the repo can forge valid JWTs for any user. Same idea for the DB password.
**Fix:** Drop the defaults so the app fails fast if the secret isn't supplied: `jwt.secret=${JWT_SECRET}`. You already gitignore `.env` and provide `.env.example` — good. Just remove the inline fallbacks. In the defense, frame it as "secrets are externalized via env vars; the defaults are a dev convenience I'd remove for production."

### H2 — No validation on registration / login DTOs
**Where:** `RegisterRequest`, `LoginRequest`, `AuthController.register/login`
**Why it matters:** `RegisterRequest` has no `@NotBlank`/`@Size` constraints and `AuthController.register` doesn't use `@Valid`. A user can register with an empty password or a one-character password (it gets BCrypt-hashed regardless). Email format is only checked on the `User` *entity*, so a bad email surfaces as a 500 from the persistence layer instead of a clean 400.
**Fix:** Add constraints to the DTO and `@Valid` on the controller:
```java
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 50) private String username;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8, max = 100) private String password;
}
// controller:
public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest req) {...}
```
You already do this correctly for `TourRequest` and `TourLogRequest` — so this is just applying your own pattern consistently.

### H3 — Lombok `@Data` on JPA entities (recursion + password in logs)
**Where:** `Tour`, `TourLog`, `User` (all annotated `@Data`)
**Why it matters:** `@Data` generates `toString()`, `equals()`, and `hashCode()` over *all* fields. Two concrete problems:
- `Tour.toString()` includes `tourLogs`, and `TourLog.toString()` includes `tour` → calling `toString()` on either (e.g. in a log line) recurses infinitely → `StackOverflowError`.
- `User.toString()` includes `password` (the BCrypt hash) — if a `User` is ever logged, the hash leaks into log files.
- `equals`/`hashCode` over mutable fields including the lazy collection is also a known JPA pitfall.
**Fix:** Replace `@Data` on entities with `@Getter @Setter`, and if you need the others, exclude the relations: `@ToString(exclude = {"tourLogs"})` / `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` keyed on `id`. This is a classic "Lombok + JPA" interview point — knowing it lands well.

### H4 — Possible NullPointerException in route lookup
**Where:** `OpenRouteServiceClient.parseRouteResponse` can `return null`; `TourService.createTour/updateTour` then call `routeInfo.getDistance()`.
**Why it matters:** If ORS responds 200 but with no `routes` array, `parseRouteResponse` returns `null`, which propagates out of `getRouteInfo`. Back in `TourService`, if the user didn't supply a distance, `routeInfo.getDistance()` throws NPE → 500. The fallback path is only used on exceptions/non-200, not on this "200 but empty" case.
**Fix:** In `parseRouteResponse`, return `createFallbackRouteInfo(...)` instead of `null` (or have the caller null-check and fall back). Also: you call `getRouteInfo` even when the user supplied *both* distance and time — wasteful and an unnecessary failure surface. Guard it: only call ORS if `distance == null || estimatedTimeMinutes == null`.

### H5 — File upload / download safety
**Where:** `FileStorageService.storeFile/loadFile`, `TourController` image endpoints
**Why it matters:** Three issues. (1) `storeFile` accepts any content type and keeps the original file extension — an attacker can upload an `.svg` containing JavaScript; `getContentType` then serves it as `image/svg+xml`, which browsers render inline → stored XSS. (2) `loadFile` has a containment check (`filePath.startsWith(uploadPath)`) but then a **fallback** `Paths.get(filename)` reads any path with no check — a path-traversal hole. (3) No file-size or magic-byte validation beyond the global 10MB multipart limit.
**Fix:** Whitelist allowed extensions/content types (png, jpg, jpeg, gif only — drop svg), validate the declared content type against the bytes, and remove the unchecked fallback branch in `loadFile`. The image endpoints also don't set `Content-Disposition`, so consider serving with `attachment` or a strict content type.

---

## Medium severity

### M1 — Inconsistent error handling; no global handler
Each controller declares its own `@ExceptionHandler` methods (and some don't cover the same exceptions). `AuthService` throws raw `RuntimeException("Username already exist")` for a duplicate — that becomes a **500**, when it should be a **409 Conflict**. **Fix:** add a single `@RestControllerAdvice` global handler covering `MethodArgumentNotValidException` (→400 with field errors), `EntityNotFoundException` (→404), a custom `DuplicateResourceException` (→409), `ResponseStatusException`, and a catch-all (→500). Remove the per-controller duplicates. This is probably the single highest-value refactor for a clean review.

### M2 — Duplicated `getCurrentUser()` in 5 services
`TourService`, `TourLogService`, `SearchService`, `StatisticsService`, `ImportExportService` each contain an identical copy. **Fix:** extract to a small `CurrentUserService`/helper bean and inject it. DRY, and a natural talking point about cohesion.

### M3 — Mapping logic scattered; static cross-service call
`SearchService.getTourResponse(...)` is a `static` method that `TourService` calls via `import static`. Tour→DTO and TourLog→DTO mapping is duplicated between `SearchService` and `TourLogService`. **Fix:** a dedicated `TourMapper`/`TourLogMapper` component (manual or MapStruct). Removes the odd service-to-service static coupling.

### M4 — CORS configured twice
You have a global `CorsConfig` bean (allows only `http://localhost:4200`) **and** `@CrossOrigin(origins = {4200, 3000})` on several controllers (but not `AuthController`/`TourLogController`). Two sources of truth that disagree. **Fix:** keep the central `CorsConfig`, delete the annotations.

### M5 — New `HttpClient` per request in ORS client
`getRouteInfo` opens a `CloseableHttpClient`, and `geocode` opens *another* one — two clients per route lookup, each created fresh. **Fix:** reuse a single pooled client (field, closed on `@PreDestroy`), or migrate to Spring's `RestClient`/`WebClient`. Performance + resource-leak hygiene.

### M6 — Import bypasses bean validation
`ImportExportService.importTours` builds entities directly from JSON and saves them — no `@Valid`, so malformed imports hit DB constraints and surface as 500s. **Fix:** validate each `TourExportData` (programmatically via a `Validator`, or map to `TourRequest` and validate) before persisting; collect and report row-level errors.

### M7 — `searchToursAdvanced` filters in memory
It loads *all* the user's tours then filters with Java streams. Fine at small scale, but it doesn't push the work to the DB. **Fix (or talking point):** a JPA `Specification`/Criteria query, or acknowledge it's an intentional simplification for the project's data volume.

---

## Low severity

- **L1 — Error messages echo `ex.getMessage()` to clients** (`SearchController`, `StatisticsController`, `ImportExportController`). Minor info leakage; return generic messages and log details server-side.
- **L2 — `MessageResponse.message` is a `public` field.** Make it private with the getter Lombok already provides.
- **L3 — `spring.jpa.hibernate.ddl-auto=update`** is convenient for dev but risky for prod schema management; mention Flyway/Liquibase as the production answer.
- **L4 — N+1 query potential** in statistics/popularity: looping tours and streaming their lazy `tourLogs` issues a query per tour. Works (open session via `@Transactional`) but won't scale; a `JOIN FETCH` or aggregate query fixes it.
- **L5 — Fallback route returns a fixed 10 km / 2 h** with only a log line. The user silently gets fabricated data. Consider surfacing a "route estimate unavailable" flag.
- **L6 — No rate limiting / lockout on login.** Out of scope for a uni project, but worth naming as a known gap.

---

## What's genuinely good (say these too)

- Clean four-layer separation that matches the architecture diagram.
- **Constructor injection everywhere** (testable, no field `@Autowired`).
- DTOs isolate the API contract from entities; responses never expose `password` or the `User` graph.
- **Ownership enforced at the data layer** — every query is `...AndUserId(...)`, so user A cannot read user B's tours even by guessing IDs (no IDOR).
- Passwords hashed with BCrypt; stateless JWT with expiry.
- Graceful degradation when the ORS API key is absent (fallback values) — the app still runs in a demo.
- 66 unit tests across services, repositories, security, and DTOs.
- Custom `DurationMinutesConverter` is a tidy solution for persisting `Duration`.
