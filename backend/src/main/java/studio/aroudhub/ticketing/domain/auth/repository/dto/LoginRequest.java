package studio.aroudhub.ticketing.domain.auth.repository.dto;

public record LoginRequest(
        String email,
        String password
) {
}
