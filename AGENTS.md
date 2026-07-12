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
- Web-layer tests that do not need DB access should explicitly run with `@ActiveProfiles("testWithoutDB")`, especially for `@WebMvcTest`-based tests.
- If a web-layer test intentionally omits `@ActiveProfiles("testWithoutDB")`, document why that test can run safely without database profile isolation.
- Auth tests that depend on database-enabled auth beans should use a database-enabled profile and mock their collaborators when appropriate.

## Docs Rules

- Check relevant documents under `docs/` when the task touches runtime profiles, local Docker setup, project structure, plans, or other documented workflows.
- Prefer the most task-relevant document first, for example `docs/runtime-profiles.md`, `docs/docker-local.md`, or `docs/project.md`.
- Treat `docs/plan/*.md` as planning/reference material unless the task explicitly requires updating implementation to match a plan.

## Reference

- Detailed profile strategy: [docs/runtime-profiles.md](docs/runtime-profiles.md)
