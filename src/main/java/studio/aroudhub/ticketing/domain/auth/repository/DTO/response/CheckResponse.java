package studio.aroudhub.ticketing.domain.auth.repository.DTO.response;

import studio.aroudhub.ticketing.domain.auth.repository.entity.UserRole;

public record CheckResponse(
        String name,
        String email,
        UserRole role
) {
}
