package studio.aroudhub.ticketing.domain.auth.repository.DTO.response;

public record UserInfo(
        String name,
        String email,
        String phone
) {

}
