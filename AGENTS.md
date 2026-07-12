# AGENTS

## Runtime Profiles

- This project uses four Spring profiles: `local`, `testWithoutDB`, `dev`, and `prod`.
- `local` runs with a local database and enables database-backed auth features.
- `dev` runs with a development database and enables database-backed auth features.
- `prod` runs with a production database and enables database-backed auth features.
- `testWithoutDB` runs without any database, datasource, JPA, or repository auto-configuration.

## Configuration Rules

- Keep profile-specific configuration in `src/main/resources/application.yaml` using profile documents separated by `---`.
- Do not hardcode datasource or JPA auto-configuration excludes in `TicketingApplication`.
- Put datasource and JPA settings under the `local`, `dev`, and `prod` profile sections.
- Put datasource/JPA/repository auto-configuration excludes under the `testWithoutDB` profile section in `src/main/resources/application.yaml`.
- Keep production secrets out of version-controlled config files. Use environment variables or external secrets.
- Keep personal information, sensitive information, credentials, and local/shared secrets out of version-controlled files. Store them in environment variables or `.env` files that are gitignored.

## Auth Rules

- `AuthController` and `AuthService` are database-dependent and should only be active in `local`, `dev`, and `prod`.
- Do not enable database-backed auth beans in `testWithoutDB`.
- `UserRepository` should be created through JPA auto-configuration only when database-enabled profiles are active.

## Testing Rules

- Use `testWithoutDB` for tests that do not require a real database.
- Write a failing test first before changing production code.
- Do not add or change production code until the missing behavior is demonstrated by a failing test.
- After making the test pass, refactor only while keeping the test suite green.
- For bug fixes, add a regression test that reproduces the bug before implementing the fix.
- Prefer the smallest reasonable test scope first: unit test, then web-layer test, then broader integration test only when necessary.
- Service-layer business logic should be covered by focused unit tests before considering broader Spring-based tests.
- Web-layer tests that do not need DB access should explicitly run with `@ActiveProfiles("testWithoutDB")`, especially for `@WebMvcTest`-based tests.
- Controller behavior should be covered with web-layer tests that verify request mapping, validation, response status, and response body shape.
- If a web-layer test intentionally omits `@ActiveProfiles("testWithoutDB")`, document why that test can run safely without database profile isolation.
- New or changed exception flows should include tests for both the expected error status and the error payload or message contract when applicable.
- When mocking collaborators, mock only true external dependencies or boundaries; do not mock the behavior of the class under test.
- Avoid assertions that are tightly coupled to call counts, internal private flow, or incidental implementation details unless that interaction is the behavior being specified.
- When adding a new endpoint or service method, cover at least one happy path and one meaningful failure path.
- Prefer behavior-focused assertions over assertions tied to internal implementation details.
- Each feature change should include automated tests for the main success path and relevant failure or edge cases.
- Do not use database-enabled profiles or wider Spring test slices when the scenario can be covered with `testWithoutDB` or a narrower test.
- Do not use `@SpringBootTest` when a narrower test such as a plain unit test or `@WebMvcTest` is sufficient.
- Auth tests that depend on database-enabled auth beans should use a database-enabled profile and mock their collaborators when appropriate.
- If a repository-backed flow truly requires a database-enabled profile, keep the covered scenario narrow and document why `testWithoutDB` is insufficient.
- Do not merge or finish a change with only manual verification when the behavior can reasonably be covered by an automated test.
- Before finishing work, run the relevant tests for the changed behavior and report what was executed.

## Docs Rules

- Check relevant documents under `docs/` when the task touches runtime profiles, local Docker setup, project structure, plans, or other documented workflows.
- Prefer the most task-relevant document first, for example `docs/runtime-profiles.md`, `docs/docker-local.md`, `docs/project.md`, or `docs/project/frontend-backend-separation.md`.
- Treat `docs/plan/*.md` as planning/reference material unless the task explicitly requires updating implementation to match a plan.

## Reference

- Detailed profile strategy: [docs/runtime-profiles.md](docs/runtime-profiles.md)
- Local Docker and frontend integration: [docs/docker-local.md](docs/docker-local.md)
- Product/domain and API reference: [docs/project.md](docs/project.md)
- Frontend/backend separation rules: [docs/project/frontend-backend-separation.md](docs/project/frontend-backend-separation.md)
