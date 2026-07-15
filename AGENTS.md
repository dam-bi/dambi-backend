# AGENTS

## General Rules

- Do not modify this file unless the task explicitly requests it.
- Before changing code, inspect the relevant implementation, tests, configuration, and documentation.
- When rules in this file overlap, follow the more specific rule.
- If a requested implementation conflicts with this file, do not silently bypass the rule. Explain the conflict and follow the higher-priority instruction.

## Runtime Profiles

This project uses three Spring profiles:

- `local`: Uses a local database and enables database-backed authentication.
- `dev`: Uses a development database and enables database-backed authentication.
- `prod`: Uses a production database and enables database-backed authentication.

Never activate the `prod` profile in automated tests.

Automated tests must never connect to a production database or a shared development database.

## Configuration Rules

- Keep profile-specific configuration in `src/main/resources/application.yaml`.
- Separate profile documents with `---`.
- Activate each profile document with `spring.config.activate.on-profile`.
- Do not hardcode datasource or JPA auto-configuration exclusions in `TicketingApplication`.
- Put datasource and JPA configuration under the `local`, `dev`, and `prod` profile documents.
- Do not commit credentials, passwords, API keys, tokens, personal data, production secrets, or machine-specific secrets.
- Use environment variables, external secret management, or gitignored `.env` files for secrets.
- Do not add real secret values to example configuration files. Use clearly named placeholders instead.

## Auth Rules

- `AuthController` and `AuthService` are active only under `local`, `dev`, and `prod`.
- Keep the profile restriction explicit with `@Profile({"local", "dev", "prod"})` or an equivalent configuration.
- In production application configuration, `UserRepository` must be created through Spring Data JPA auto-configuration.
- Do not add production configuration that defines a fake or in-memory `UserRepository`.
- Test-local Mockito mocks of `UserRepository` are allowed outside repository tests.

## Test-First Development

A behavior change includes adding or changing an API response, validation rule, business rule, error response, persistence behavior, or other externally observable result.

For behavior changes and bug fixes, follow red-green-refactor:

1. Add or update an automated test that demonstrates the missing or incorrect behavior.
2. Run the test and confirm that it fails for the expected reason.
3. Make the smallest reasonable production-code change that passes the test.
4. Refactor only while keeping the relevant tests green.

Do not change production behavior before the failing test has demonstrated the requirement.

For documentation-only, formatting-only, or non-behavioral configuration changes, a new failing test is required only when the change can be meaningfully verified through automation.

For bug fixes, keep the reproducing test as a regression test.

## Test Scope Rules

Use the smallest test scope that can verify the required behavior:

1. Plain unit test
2. Web-layer or persistence test slice
3. Broader Spring integration test
4. Full application test only when necessary

Use `@SpringBootTest` only when the tested behavior requires multiple Spring layers to interact and cannot be verified with a unit test or a narrower test slice.

### Service Unit Tests

- Test service-layer business logic with plain JUnit and Mockito where possible.
- Instantiate the class under test directly.
- Do not start a Spring ApplicationContext when Spring behavior is not part of the test.
- Mock dependencies that are outside the scope of the unit test.
- Do not mock the class under test.
- Service unit tests should not activate a Spring profile unless profile behavior is part of the test.
- In an `AuthService` unit test, `AuthService` is the class under test.
- In an `AuthService` unit test, mock dependencies such as `UserRepository` and `PasswordEncoder`.

### Web-Layer Tests

- Use `@WebMvcTest` when testing controller behavior without loading the entire application.
- Mock the controller's service-layer dependencies with `@MockitoBean`.
- Verify request mapping, request parsing, validation, response status, content type, and response body contract.
- Do not connect web-layer tests to a real database when the service dependency can be mocked.

### Auth Web-Layer Tests

- Use `@WebMvcTest(AuthController.class)`.
- Activate `local` or `dev` only when required to satisfy the controller's profile condition.
- When `dev` is activated in a web-layer test, it is used only for profile-based bean registration and must not cause a database connection.
- Replace `AuthService` with `@MockitoBean`.
- Do not import datasource, JPA, or repository configuration into an Auth web-layer test.
- Never activate `prod`.
- An Auth web-layer test must not create a datasource or connect to any real database.

### Repository and Database Integration Tests

- Use a narrow persistence test such as `@DataJpaTest` when possible.
- Do not activate `dev` or `prod` for repository tests.
- Do not connect repository tests to a developer's persistent local database.
- Use an isolated and disposable test database.
- A disposable test database is created specifically for the test run and removed after the test run.
- Prefer Testcontainers when real PostgreSQL behavior is required.
- When using Testcontainers, override all datasource properties with container-provided values.
- Prefer `@DynamicPropertySource` or an equivalent dynamic datasource configuration.
- Do not mock the repository being tested.
- Mock another collaborator only when that collaborator is outside the behavior covered by the repository or database integration test.
- Document why a real database is required when a repository-backed scenario cannot be covered by a unit test.

## Test Coverage Rules

Each behavior change must include automated coverage for:

- The primary success path
- At least one meaningful failure or edge case when such a path exists
- Validation failures when request validation is involved
- Exception behavior when the change adds or modifies an exception flow
- HTTP status and error payload when the API exposes an error response contract

For a new or changed endpoint, verify:

- HTTP method and request mapping
- Request parsing and validation
- Response status
- Response content type
- Response body structure and relevant field values

Prefer behavior-focused assertions.

Avoid assertions tied to private implementation details, incidental call counts, or internal control flow unless that interaction is itself part of the required behavior.

Do not rely only on manual verification when the behavior can reasonably be covered by an automated test.

## Test Execution

Run the narrowest relevant test first.

Unix or Codex environment:

```bash
./gradlew test --tests "fully.qualified.TestClass"
./gradlew test
```

Windows local environment:

```powershell
gradlew.bat test --tests "fully.qualified.TestClass"
gradlew.bat test
```

For a controller change, run the controller test and related service unit tests.

For shared configuration, runtime profile, application startup, security configuration, or multi-layer behavior changes, run the full test suite.

Before finishing:

- Run all tests directly related to the changed behavior.
- Review the final diff for unintended production, profile, database, or secret-related changes.
- Do not claim that the test suite is green when any executed test failed.
- Distinguish failures caused by the current change from pre-existing failures.
- Report pre-existing failures with the failing test name and a short error summary.
- Report the exact test commands executed and whether they passed.
- If a relevant test could not be executed, report the reason instead of claiming the change is verified.

For behavior changes and bug fixes, report:

- The test that was added or changed
- The command used to run it before the production change
- The expected reason it failed
- The command used after the production change
- Whether it passed

## Documentation Rules

Check the document most directly related to the task before changing documented behavior:

- Runtime profile behavior: `docs/runtime-profiles.md`
- Local Docker and frontend integration: `docs/docker-local.md`
- Product, domain, and API behavior: `docs/project.md`
- Frontend/backend repository separation: `docs/project/frontend-backend-separation.md`

Treat `docs/plan/*.md` as planning and reference material unless the task explicitly requires implementing or updating a plan.

When implementation changes documented behavior, update the relevant documentation in the same change.

## Completion Criteria

A code change is complete only when:

- The requested behavior is implemented.
- Required tests have been added or updated.
- A behavior change or bug fix was demonstrated by a failing test before the production fix.
- Relevant tests pass.
- No automated test uses the `prod` profile.
- No automated test connects to a production database, shared development database, or persistent developer-local database.
- Profile behavior remains consistent with this file and the relevant documents.
- No credentials or secrets were added to version-controlled files.
- The final response reports the files changed and the tests executed.