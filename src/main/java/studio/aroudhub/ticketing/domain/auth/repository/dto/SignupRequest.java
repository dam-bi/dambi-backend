package studio.aroudhub.ticketing.domain.auth.repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
