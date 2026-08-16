# AGENTS

## Karpathy Guidelines

- Follow [docs/project/karpathy-guidelines.md](docs/project/karpathy-guidelines.md) for tasks in this repository.
- Think before coding: surface assumptions, ambiguities, and simpler alternatives instead of deciding silently.
- Prefer the simplest solution that satisfies the current request. Do not add speculative abstractions or features.
- Make surgical changes only. Avoid unrelated refactors, formatting churn, or cleanup outside the requested scope.
- Execute with verifiable goals. Define concrete success criteria and prefer test-first verification for behavior changes.

## Comment Rules

- Write all comments in Korean.
- Keep original English for identifiers, class names, method names, API names, library names, and other fixed technical terms when needed.
- For every new or modified method, add a short Korean comment above the declaration that explains the method's role.
- Method comments should describe the feature, intent, key input/output expectations, or important usage constraints, not restate the implementation line by line.
- Add concise Korean comments for complex logic, branching, exception handling, performance constraints, or domain rules that are not obvious from the code alone.
- Do not add comments that merely restate trivial code, translate the code mechanically, or explain each line.

## Runtime Profiles

- This project uses three Spring profiles: `local`, `dev`, and `prod`.
- `local` runs with a local database and enables database-backed auth features.
- `dev` runs with a development database and enables database-backed auth features.
- `prod` runs with a production database and enables database-backed auth features.

## Configuration Rules

- Keep profile-specific configuration in `src/main/resources/application.yaml` using profile documents separated by `---`.
- Do not hardcode datasource or JPA auto-configuration excludes in `TicketingApplication`.
- Put datasource and JPA settings under the `local`, `dev`, and `prod` profile sections.
- Keep production secrets out of version-controlled config files. Use environment variables or external secrets.
- Keep personal information, sensitive information, credentials, and local/shared secrets out of version-controlled files. Store them in environment variables or `.env` files that are gitignored.

## Auth Rules

- `AuthController`, `AuthService`, and `UserRepository` require a database-enabled profile.

## Testing Rules

- Write a failing test first before changing production code.
- Do not add or change production code until the missing behavior is demonstrated by a failing test.
- After making the test pass, refactor only while keeping the test suite green.
- For bug fixes, add a regression test that reproduces the bug before implementing the fix.
- Prefer the smallest reasonable test scope first: unit test, then web-layer test, then broader integration test only when necessary.
- Service-layer business logic should be covered by focused unit tests before considering broader Spring-based tests.
- Controller behavior should be covered with web-layer tests that verify request mapping, validation, response status, and response body shape.
- New or changed exception flows should include tests for the expected HTTP status or error type. Do not require response body message assertions unless the body contract itself is the behavior being changed.
- When mocking collaborators, mock only true external dependencies or boundaries; do not mock the behavior of the class under test.
- Avoid assertions that are tightly coupled to call counts, internal private flow, or incidental implementation details unless that interaction is the behavior being specified.
- When adding a new endpoint or service method, cover at least one happy path and one meaningful failure path.
- Prefer behavior-focused assertions over assertions tied to internal implementation details.
- Each feature change should include automated tests for the main success path and relevant failure or edge cases.
- Do not use `@SpringBootTest` when a narrower test such as a plain unit test or `@WebMvcTest` is sufficient.
- Auth tests that depend on database-enabled auth beans should use a database-enabled profile and mock their collaborators when appropriate.
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
