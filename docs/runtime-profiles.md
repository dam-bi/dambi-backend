# Runtime Profiles

## Purpose

This project separates runtime behavior by Spring profile so each runtime environment can provide its own database configuration without forcing temporary code changes.

The current profile plan is:

- `local`: local development with a local database
- `dev`: shared development environment with database
- `prod`: production environment with database

## Why This Split Exists

Some parts of the application depend on JPA repositories and a datasource. A representative example is the auth module:

- `AuthService` depends on `UserRepository`
- `UserRepository` is created by Spring Data JPA
- Spring Data JPA needs datasource and JPA auto-configuration

Database behavior must be controlled by profile instead of by hardcoded exclusions in the application entry point.

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

3. `dev` section
   - development datasource settings
   - development JPA settings

4. `prod` section
   - production datasource settings
   - production JPA settings
   - no hardcoded secrets in git-managed files

## Bean Activation Policy

Database-dependent beans require a database-enabled profile:

- `local`
- `dev`
- `prod`

`AuthController`, `AuthService`, and `UserRepository` use the datasource and JPA auto-configuration supplied by those profiles.

## Testing Policy

Use a focused unit test or web-layer test when the scenario does not require a real database connection. Use a database-enabled profile only when the test genuinely requires database-backed beans or JPA behavior.

## Implementation Notes

- Remove global datasource/JPA exclude configuration from `TicketingApplication`
- Prefer profile-based configuration over temporary commenting or ad hoc startup workarounds
