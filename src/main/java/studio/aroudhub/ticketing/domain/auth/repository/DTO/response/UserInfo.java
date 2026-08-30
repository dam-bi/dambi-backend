package studio.aroudhub.ticketing.domain.auth.repository.DTO.response;

import studio.aroudhub.ticketing.domain.auth.repository.entity.UserRole;

public record UserInfo(
        String name,
        String email,
        String phone,
        UserRole userRole
) {

}
