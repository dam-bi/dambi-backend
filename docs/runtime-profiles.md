# Runtime Profiles

## Purpose

This project separates runtime behavior by Spring profile so the application can run in both database-enabled and database-disabled environments without forcing temporary code changes.

The current profile plan is:

- `local`: local development with a local database
- `testWithoutDB`: test mode without database wiring
- `dev`: shared development environment with database
- `prod`: production environment with database

## Why This Split Exists

Some parts of the application depend on JPA repositories and a datasource. A representative example is the auth module:

- `AuthService` depends on `UserRepository`
- `UserRepository` is created by Spring Data JPA
- Spring Data JPA needs datasource and JPA auto-configuration

If datasource/JPA auto-configuration is disabled globally, repository beans are not created and database-dependent beans fail at startup.

To avoid that, database behavior must be controlled by profile instead of by hardcoded exclusions in the application entry point.

## Configuration Strategy

Use a single `application.yml` file with multiple profile documents separated by `---`.

Recommended layout:

1. Common section
   - application name
   - shared server settings
   - shared logging settings

2. `local` section
   - local datasource settings
   - local JPA settings

3. `testWithoutDB` section
   - datasource auto-configuration exclude
   - Hibernate JPA auto-configuration exclude
   - Spring Data JPA repository auto-configuration exclude

4. `dev` section
   - development datasource settings
   - development JPA settings

5. `prod` section
   - production datasource settings
   - production JPA settings
   - no hardcoded secrets in git-managed files

## Bean Activation Policy

Database-dependent beans should only activate in profiles that have a database:

- `local`
- `dev`
- `prod`

Database-disabled profile:

- `testWithoutDB`

Current guidance:

- `AuthController` should only be active in database-enabled profiles
- `AuthService` should only be active in database-enabled profiles
- `UserRepository` should rely on JPA auto-configuration and does not need its own profile annotation

## Testing Policy

Use `testWithoutDB` when the test does not require a real database connection.

Examples:

- controller tests for non-DB endpoints
- service tests with mocked dependencies
- MVC slice tests that should not load JPA infrastructure

Use a database-enabled profile only when the test genuinely requires database-backed beans or JPA behavior.

## Implementation Notes

- Remove global datasource/JPA exclude configuration from `TicketingApplication`
- Move database exclusion into the `testWithoutDB` profile document in `application.yml`
- Keep auth-related beans out of `testWithoutDB`
- Prefer profile-based activation over temporary commenting or ad hoc startup workarounds
