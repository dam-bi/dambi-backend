package studio.aroudhub.ticketing.domain.auth.repository.DTO.response;

public record LoginResponse(
        String accessToken,
        UserInfo userInfo
) {
}
