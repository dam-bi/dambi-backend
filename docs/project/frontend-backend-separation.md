# Frontend / Backend Separation

## Purpose

This project uses a fully separated architecture:

- Frontend: separate React application
- Backend: Spring Boot API server

The backend is responsible for JSON APIs only. It should not serve frontend pages for concert, event, booking, or other user-facing screens.

## Rules

- Do not add or maintain `index.html` as an application page entry inside the backend project.
- Do not implement page-rendering controllers such as returning `"concert"` or other view names from domain controllers.
- Domain controllers should expose API endpoints only, for example:
  - `GET /api/concerts`
  - `GET /api/concerts/{id}`
  - `GET /api/events`
- React frontend routes such as `/concerts` and `/concerts/:id` must be handled in the frontend project, not in the backend.
- Backend responses should provide the data needed for the frontend to compose screens.

## Backend Responsibilities

- Validate requests
- Read and write database state
- Apply business rules
- Return JSON request/response payloads
- Expose auth, concert, event, booking, reservation, and payment APIs

## Frontend Responsibilities

- Own page routing
- Call backend APIs
- Render list/detail screens from API responses
- Handle loading, empty, error, and interaction states

## Concert Implementation Guidance

- `ConcertController` must remain an API controller.
- Concert list and detail pages are frontend concerns.
- If concert UI work is needed in this repository, limit the work to response shape and API behavior required by the React frontend.

## Implications for Current Code

- Backend-side SPA forwarding is not part of the target architecture.
- Static page files under `src/main/resources` that exist only to render frontend routes should be removed or avoided.
- Future concert work should be discussed in terms of:
  - required endpoint
  - request parameters
  - response JSON shape
  - error handling

