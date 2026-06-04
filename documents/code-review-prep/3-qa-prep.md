# Oral Defense Q&A Prep — TourPlanner Backend

Likely instructor questions with answers grounded in *your* code. Read the answer, then re-say it in your own words — examiners can tell rehearsed-from-memory from understood. Where a question targets a known weakness, the answer both admits it and shows you know the fix (that combination scores well).

---

## Architecture & design

**Q: Walk me through the architecture of your backend.**
A: It's a layered architecture. Controllers handle HTTP and validation, services hold the business logic and transactions, repositories handle data access via Spring Data JPA, and PostgreSQL stores the data. Dependencies only point downward, and entities never leave the service layer — controllers exchange DTOs. There's also an integration layer (`OpenRouteServiceClient`) for the external routing API, and cross-cutting security via a JWT filter.

**Q: Why separate DTOs from entities? Isn't that extra boilerplate?**
A: It decouples the API contract from the database schema. I can change a column without breaking clients, and I never accidentally serialize sensitive fields — for instance `password` lives on the `User` entity but no response DTO exposes it. Validation annotations live on the request DTOs, so bad input is rejected at the edge.

**Q: Why constructor injection instead of `@Autowired` fields?**
A: It makes dependencies explicit and final, the class can't be constructed in an invalid state, and it's trivial to unit test — I just pass mocks to the constructor, which is exactly what my service tests do.

**Q: Where does business logic live, and why not in the controller?**
A: In the services. Keeping controllers thin means the logic is reusable and testable independent of the web layer, and the HTTP concerns (status codes, headers) stay separate from domain rules like ownership checks and the child-friendliness calculation.

---

## Security

**Q: How does authentication work?**
A: Stateless JWT. On login I verify the password with BCrypt and issue a signed token (HMAC-SHA, 24-hour expiry). The client sends it as a Bearer header. A `OncePerRequestFilter` validates the token on every request, loads the user, and populates the Spring `SecurityContext`. `SecurityConfig` permits `/api/auth/**` and requires authentication for everything else, with sessions set to STATELESS.

**Q: Why is CSRF disabled? Isn't that insecure?**
A: CSRF protection matters when the browser auto-sends credentials, i.e. cookies. I authenticate with a Bearer token in a header that the client sets explicitly, so a cross-site request can't ride along automatically. Disabling CSRF for a stateless token API is the standard, correct choice.

**Q: How do you stop one user from reading another user's tours?**
A: Every repository call is scoped by user — `findByIdAndUserId`, `findByUserId`, etc. The service resolves the authenticated user from the security context and passes their ID into the query. So even if user A guesses user B's tour ID, the query returns empty and they get a 404. It closes the insecure-direct-object-reference (IDOR) gap.

**Q: How are passwords stored?**
A: BCrypt-hashed via Spring Security's `BCryptPasswordEncoder` — salted and adaptive. The plaintext is never stored or logged.

**Q: (Likely challenge) Your JWT secret is in `application.properties`. Comment?**
A: Good catch — it's externalized as an env var (`${JWT_SECRET:...}`), but I left a committed fallback default for dev convenience. For production I'd remove the default so the app refuses to start without a real secret, and the same for the DB password. The `.env` file with real secrets is gitignored; only `.env.example` is committed. *(This is finding H1 — owning it beats being caught.)*

**Q: What happens with an invalid or expired token?**
A: The filter catches parsing/validation failures, logs a warning, and simply doesn't set an authentication. The request then hits Spring Security's authorization rules and is rejected with 401/403 for any protected endpoint.

---

## Persistence & JPA

**Q: How do you store a `Duration`?**
A: A custom JPA `AttributeConverter` (`DurationMinutesConverter`) that stores it as a `Long` of minutes and converts back on read. JPA has no built-in `Duration` mapping, so this keeps the entity clean while the DB column stays a simple integer.

**Q: What does `cascade = ALL` and `orphanRemoval = true` on `Tour.tourLogs` do?**
A: Logs belong to a tour, so when I delete a tour its logs are deleted too, and removing a log from the collection deletes it from the DB. It models the parent-child ownership correctly.

**Q: Why `FetchType.LAZY` on the relationships?**
A: To avoid loading the whole object graph on every query. Logs are only fetched when I actually touch the collection, inside the transaction. The trade-off is I must access them within an open session, which my `@Transactional` services guarantee.

**Q: (Likely challenge) Could that cause N+1 queries?**
A: Yes — in statistics I loop tours and read each one's logs, which can issue a query per tour. It's fine at this project's scale, but the proper fix is a `JOIN FETCH` or an aggregate query, which I'd do if the data grew.

**Q: How are your custom searches implemented?**
A: Two ways. Simple search uses JPQL `@Query` methods with case-insensitive `LIKE` across several columns, scoped by user. The advanced filtered search currently filters in memory with Java streams — simpler to read, and acceptable here, though a JPA `Specification` would push it to the DB for large datasets.

---

## Patterns

**Q: What design patterns did you use?**
A: A few. **Repository** (Spring Data abstracts data access). **DTO** (separating transfer objects from entities). **Builder** (hand-written builders on the request/response DTOs for readable test data and mapping). **Dependency injection** throughout. The `OpenRouteServiceClient` is an **adapter/gateway** wrapping the external API behind a clean `RouteInfo` interface. The `@Transient` computed getters (popularity, child-friendliness) keep derived domain logic on the entity.

**Q: Your commit mentions a Factory pattern — where?**
A: *(Check honestly before the review.)* The transport-type→routing-profile mapping in `OpenRouteServiceClient.mapTransportTypeToProfile` is factory-like — it selects the right routing strategy from a key. If you implemented an explicit factory elsewhere, point to it; if not, describe this as the closest thing and don't overclaim.

**Q: Why a custom builder instead of Lombok's `@Builder`?**
A: It was a deliberate, explicit implementation. Lombok's `@Builder` would remove the boilerplate and I'd be comfortable switching to it — the behavior is the same.

---

## Error handling

**Q: How do you handle errors and return proper status codes?**
A: Validation failures from `@Valid` produce 400s; `EntityNotFoundException` is mapped to 404 by controller `@ExceptionHandler` methods; login failures throw `ResponseStatusException(401)`. *(Be ready for the follow-up.)*

**Q: (Likely challenge) A duplicate username returns 500 — should it?**
A: No — it should be 409 Conflict. Right now `AuthService` throws a raw `RuntimeException`, which falls through to a 500. I'd add a custom `DuplicateResourceException` and a single `@RestControllerAdvice` global handler that maps each exception type to the right status, replacing the per-controller handlers. That's my top refactor.

---

## Testing

**Q: How did you test the backend?**
A: 66 unit tests across layers — services (with mocked repositories), repositories (with an H2 in-memory DB), the JWT service, the auth controller, and DTO validation. Services are tested in isolation thanks to constructor injection, and repository tests verify the owner-scoped queries actually filter by user.

**Q: How do you test code that calls the external API?**
A: The ORS client is isolated behind `OpenRouteServiceClient`, and `TourService` tests mock it, so the business logic is tested without real network calls. The client itself has its own focused tests.

**Q: What would you add with more time?**
A: Integration tests with `@SpringBootTest` and MockMvc to exercise the full HTTP→DB path, plus negative-path tests (expired token, ownership violations, malformed import files). And I'd raise coverage on the import/export edge cases.

---

## External API & resilience

**Q: What if OpenRouteService is down or you have no API key?**
A: The client degrades gracefully — it logs the problem and returns fallback distance/time so the app keeps functioning. *(Then volunteer the fix:)* The honest weakness is that the fallback is silent fixed data; I'd surface an "estimate unavailable" flag, and there's an edge case where a 200-but-empty response could cause an NPE that I'd guard by returning the fallback there too.

**Q: Why Apache HttpClient and not RestTemplate/WebClient?**
A: It was a straightforward choice for the REST calls. A cleaner option in the Spring ecosystem is `RestClient` (or `WebClient`), which I'd move to — and I'd reuse a single pooled client instead of creating one per call, which is a small inefficiency in the current code.

---

## The "what would you improve?" question (almost guaranteed)

Have three ready, in priority order:
1. **One global exception handler** (`@RestControllerAdvice`) so status codes are consistent and the duplicate-username case returns 409.
2. **Tighten validation and config** — `@Valid` on registration with size constraints, and remove the committed secret/password defaults.
3. **Centralize entity↔DTO mapping** in a dedicated mapper and extract the duplicated `getCurrentUser()` into one bean.

Naming your own weaknesses, with fixes, is the strongest thing you can do in a defense.
