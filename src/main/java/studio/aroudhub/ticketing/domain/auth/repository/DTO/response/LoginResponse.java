package studio.aroudhub.ticketing.domain.auth.repository.DTO.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginResponse(
        String accessToken,
        UserInfo userInfo,
        @JsonIgnore String refreshToken
) {
}
