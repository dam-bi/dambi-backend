package studio.aroudhub.ticketing.domain.auth.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    @Test
    // 유효한 access token에서 이메일을 읽고 만료되지 않았음을 확인한다.
    void generateAndParseAccessToken() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("c2VjdXJlLXRlc3Qta2V5LWZvci1qd3QtdGVzdGluZy0xMjM0NTY=", 3600L);

        String token = jwtTokenProvider.generateToken("alice@example.com");

        assertThat(jwtTokenProvider.getEmail(token)).isEqualTo("alice@example.com");
        assertThat(jwtTokenProvider.isTokenExpired(token)).isFalse();
    }

    @Test
    // 잘못된 형식의 token은 파싱 단계에서 거부한다.
    void invalidToken_isRejected() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("c2VjdXJlLXRlc3Qta2V5LWZvci1qd3QtdGVzdGluZy0xMjM0NTY=", 3600L);

        assertThatThrownBy(() -> jwtTokenProvider.getEmail("invalid.token.value"))
                .isInstanceOf(RuntimeException.class);
    }
}
