package studio.aroudhub.ticketing.domain.auth.repository.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        String email,
        String password
) {
}
