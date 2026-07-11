package studio.aroudhub.ticketing.domain.auth.repository.dto;

public record AuthResponse(
        boolean isSuccess,
        String message
) {
}
