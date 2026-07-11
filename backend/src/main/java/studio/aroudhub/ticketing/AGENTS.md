/# Repository Guidelines

## Project Structure & Module Organization
This package contains the main Spring Boot application code under `src/main/java/studio/aroudhub/ticketing`. `TicketingApplication.java` is the application entry point. Shared configuration lives in `config/` and `global/config/`. Business code is organized by feature under `domain/`, for example `domain/event`, `domain/concert`, and `domain/home`, typically split into `controller`, `service`, `repository`, and `repository/entity`.

Keep new code inside the matching feature package instead of creating cross-cutting utility folders. Mirror this package structure in tests under `src/test/java/studio/aroudhub/ticketing`.

## Build, Test, and Development Commands
Run commands from the current repository root.

- `./gradlew bootRun` or `mvn spring-boot:run`: start the application locally.
- `./gradlew test` or `mvn test`: run the full test suite.
- `./gradlew build` or `mvn clean package`: compile, test, and produce a build artifact.

Use the build tool already configured in this repository. Do not add parallel build systems.

## Coding Style & Naming Conventions
Use 4-space indentation and standard Java formatting. Class names use `PascalCase`; methods and fields use `camelCase`; constants use `UPPER_SNAKE_CASE`. Name Spring components by role: `EventController`, `EventService`, `EventRepository`, `Event`.

Prefer small feature-focused classes. Keep request handling in controllers, business rules in services, and persistence logic in repositories. Follow existing package naming: lowercase, singular/shared prefixes such as `domain`, `config`, and `global`.

## Project Coding Rule
Follow the repository-level rules in `AGENTS.md` when changing application code. Treat that file as the source of truth for project-specific implementation boundaries.

Start with documents before code. Check `docs/project.md` for domain rules, states, APIs, and response shapes, and consult `docs/project/karpathy-guidelines.md` when planning or changing code. If docs and code disagree, resolve the source of truth first instead of guessing. Do not expand undefined requirements on your own.

Keep responsibilities strict:

- Controllers handle HTTP mapping, validation, and response shaping only.
- Services own business rules, transactions, state changes, and cross-repository orchestration.
- Repositories handle persistence and queries only.

Do not return entities directly from APIs, do not call repositories from controllers, and do not place business decisions in repositories. Repositories should return entities or query-focused projections, not request or response DTOs.

Make changes surgically. Avoid unrelated refactors, keep existing package and style conventions, and clean up any imports, DTO mappings, or tests broken by your change in the same task. If nearby issues are outside scope, report them instead of folding them into the change.

For multi-step work, write a short plan first. Each step should state what changes and how it will be verified, for example: compare `docs/project.md` to current code, make the smallest targeted edit, then confirm with tests or endpoint behavior.

## Testing Guidelines
Place tests in the matching package under `src/test/java`. Name test classes with the `*Test` suffix, for example `EventServiceTest`. Cover controller responses, service logic, and repository behavior where applicable.

For bug fixes, add or update a regression test with the code change. Run `./gradlew test` or `mvn test` before opening a PR.

## Commit & Pull Request Guidelines
Recent commits are short and task-focused, often with a scope prefix such as `[event]` or `[project]`. Follow that pattern and keep each commit limited to one concern.

PRs should include a short summary, linked issue or task ID, affected endpoints or packages, and sample request/response details for API changes. Include screenshots only when UI behavior changes.
