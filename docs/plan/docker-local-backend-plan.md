# Docker Local Backend Plan

## Goal

Frontend developers run the backend server and PostgreSQL on their own PC with Docker, while running frontend code locally on the host machine.

The target developer experience is:

1. Clone the backend repository from GitHub.
2. Run `docker compose up --build`.
3. Start the frontend locally on the host PC.
4. Call backend APIs through `http://localhost:8080`.
5. Test login, signup, event, concert, and other API flows against the Dockerized backend and PostgreSQL database.

## Scope

This Docker setup must include:

- Cloned backend source code in the repository
- Backend application container
- PostgreSQL container
- Database initialization setup
- Schema and required tables
- Optional seed data for frontend testing
- Documentation for frontend developers

This setup is for local development and frontend integration testing, not production deployment.

## Runtime Profile Strategy

Use the `local` Spring profile for the Dockerized backend.

Reason:

- `local` already enables datasource, JPA, repository, and database-backed auth
- `testWithoutDB` is only for tests that must run without DB/JPA/repository auto-configuration
- `dev` and `prod` should not be used for frontend developers' local Docker environments

Required backend environment variables:

- `SPRING_PROFILES_ACTIVE=local`
- `LOCAL_DB_URL=jdbc:postgresql://postgres:5432/ticketing`
- `LOCAL_DB_USERNAME=postgres`
- `LOCAL_DB_PASSWORD=<local password>`
- `LOCAL_DB_DRIVER=org.postgresql.Driver`

## Configuration Principle

Database-related settings must be externalized through environment variables or `.env`-style files.

Rules:

- do not hardcode local DB credentials in source code
- do not commit real local or shared passwords into version-controlled config files
- keep `application.yaml` as a placeholder-based configuration file
- provide sample values through `.env.example`
- keep real local values in `.env`, `.env.docker`, or another gitignored env file

This project already supports this pattern through placeholder-based datasource properties in `application.yaml`.

## Target Architecture

### Host machine

- Frontend source code runs locally
- Browser accesses frontend locally
- Frontend calls backend APIs through `http://localhost:8080`

### Docker Compose

- `postgres` container
- `backend` container
- named volume for PostgreSQL persistence
- optional bind mount or image build from repository source

### Network flow

- Host frontend -> `http://localhost:8080`
- Backend container -> `postgres:5432`

## Container Design

### 1. PostgreSQL container

Responsibilities:

- Run PostgreSQL for local frontend integration testing
- Create and persist the `ticketing` database
- Apply initialization SQL on first startup
- Preserve data using a Docker volume

Required settings:

- database name: `ticketing`
- username: `postgres`
- password: local development password managed through env file
- exposed port: `5432:5432` only if direct DB access is needed from host tools

Recommended additions:

- `healthcheck` for database readiness
- `db/init/` directory mounted into `docker-entrypoint-initdb.d`

### 2. Backend container

Responsibilities:

- Build and run the Spring Boot backend
- Connect to PostgreSQL over the Docker network
- Expose backend APIs to frontend developers on the host machine

Required settings:

- active profile: `local`
- port mapping: `8080:8080`
- datasource values from environment variables
- startup dependency on PostgreSQL readiness

Recommended additions:

- multistage `Dockerfile`
- backend `healthcheck`
- restart policy suitable for local development

## Database Initialization Strategy

The Docker environment must include database setup, table creation, and baseline test data.

Recommended order of preference:

1. Migration-based schema management such as Flyway
2. SQL initialization scripts committed in the repository
3. JPA `ddl-auto=update` only as a temporary fallback

Preferred direction:

- Keep reproducible schema creation in version-controlled SQL or migrations
- Avoid relying only on `ddl-auto=update` for shared frontend testing environments

Initialization should cover:

- core tables required by auth and domain features
- test user accounts for login flows
- sample event, concert, and venue data if frontend screens need them

## Frontend Developer Workflow

### Expected workflow

1. Clone the backend repository
2. Prepare local env file if needed
3. Run `docker compose up --build`
4. Wait until backend and PostgreSQL are healthy
5. Run frontend locally
6. Configure frontend API base URL as `http://localhost:8080`
7. Test API integration from the browser

### Expected benefits

- no local Java or PostgreSQL installation required beyond Docker
- backend and DB setup are standardized across frontend developers
- login and other DB-backed flows are testable
- local data persists across container restarts

## Repository Files To Add

The Docker plan should be implemented with these files:

- `Dockerfile`
- `compose.yml` or `docker-compose.yml`
- `.env.example`
- optional `.env.docker` for local execution
- `db/init/*.sql` or migration files
- `docs/docker-local.md` or equivalent run guide

## Environment Variable Policy

Recommended split:

- commit `.env.example`
- ignore `.env`
- ignore `.env.docker`

Example categories:

- backend app variables
  - `SPRING_PROFILES_ACTIVE`
  - `LOCAL_DB_URL`
  - `LOCAL_DB_USERNAME`
  - `LOCAL_DB_PASSWORD`
  - `LOCAL_DB_DRIVER`
- postgres container variables
  - `POSTGRES_DB`
  - `POSTGRES_USER`
  - `POSTGRES_PASSWORD`

Recommended behavior:

- frontend developers copy `.env.example` to `.env.docker` or `.env`
- Compose reads the real values from the local env file
- repository keeps only example values, not actual credentials

## Proposed Compose Services

### `postgres`

Should define:

- image: PostgreSQL stable version approved by team
- container name
- environment variables for DB name, user, password
- volume for persistent data
- init script mount
- optional host port exposure
- healthcheck

### `backend`

Should define:

- build from current repository
- container name
- depends_on with DB health condition if supported
- environment variables for `local` profile
- port `8080`
- optional healthcheck

## API Access Rules

Frontend developers should access the backend from the host machine through:

- `http://localhost:8080`

The backend container should access PostgreSQL through:

- `jdbc:postgresql://postgres:5432/ticketing`

This separation is important:

- `localhost` is for host-to-container access
- `postgres` is for container-to-container access inside Docker Compose

## CORS Considerations

Current backend configuration allows broad CORS access, so local frontend integration should work without extra Docker-specific CORS changes.

Later hardening can restrict allowed origins to known local frontend ports such as:

- `http://localhost:3000`
- `http://localhost:5173`

For the initial Docker workflow, broad local development compatibility is acceptable.

## Data Persistence Policy

Use a named Docker volume for PostgreSQL data.

Expected behavior:

- `docker compose down` keeps database data
- `ㅍ` resets the database completely

This gives frontend developers both:

- persistent local test data during ongoing work
- a simple full reset path when needed

## Health and Readiness

Recommended readiness checks:

- PostgreSQL healthcheck in Compose
- backend health endpoint such as `/actuator/health`

If Actuator is added later, frontend developers and backend developers can quickly verify whether the server is actually ready before testing.

## Security Boundaries

This environment is for local development only.

Rules:

- do not store production secrets in repository files
- keep local development credentials in env files or Compose-local values
- do not use `prod` profile in frontend local Docker workflows

## Implementation Plan

### Phase 1. Docker baseline

- add `Dockerfile`
- add Compose file
- define backend and PostgreSQL services
- wire `local` profile environment variables

### Phase 2. Database reproducibility

- add DB initialization scripts or migrations
- create baseline schema
- add test user and sample domain data

### Phase 3. Developer documentation

- document startup steps
- document API base URL
- document reset procedure
- document test account credentials

### Phase 4. Optional hardening

- add Actuator health endpoint
- tighten CORS for local frontend origins
- replace schema drift behavior with managed migrations if not already done

## Acceptance Criteria

The plan is complete when a frontend developer can:

1. Clone the repository
2. Run `docker compose up --build`
3. Access backend APIs on `localhost:8080`
4. Use frontend code on the host machine to call the API
5. Use PostgreSQL-backed auth and domain APIs
6. Restart containers without losing data unintentionally
7. Fully reset local data when needed

## Recommended Next Work

Implementation work should proceed in this order:

1. Create `Dockerfile`
2. Create `compose.yml`
3. Add env example file
4. Add DB initialization or migration files
5. Add local Docker run guide
6. Validate with actual `docker compose up --build`
