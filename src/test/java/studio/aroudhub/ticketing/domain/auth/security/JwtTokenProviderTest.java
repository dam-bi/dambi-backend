package studio.aroudhub.ticketing.domain.auth.security;

import org.junit.jupiter.api.Test;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String ACCESS_SECRET = "c2VjdXJlLXRlc3Qta2V5LWZvci1qd3QtdGVzdGluZy0xMjM0NTY=";
    private static final String REFRESH_SECRET = "cmVmcmVzaC10ZXN0LWtleS1mb3Itand0LXRlc3RpbmctMTIzNDU2";

    @Test
    // ?좏슚??access token?먯꽌 ?대찓?쇱쓣 ?쎄퀬 留뚮즺?섏? ?딆븯?뚯쓣 ?뺤씤?쒕떎.
    void generateAndParseAccessToken() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(ACCESS_SECRET, REFRESH_SECRET, 3600L, 7200L);

        String token = jwtTokenProvider.generateAccessToken("alice@example.com");

        assertThat(jwtTokenProvider.getEmail(token)).isEqualTo("alice@example.com");
        assertThat(jwtTokenProvider.isAccessTokenExpired(token)).isFalse();
    }

    @Test
    // ?섎せ???뺤떇??token? ?뚯떛 ?④퀎?먯꽌 嫄곕??쒕떎.
    void invalidToken_isRejected() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(ACCESS_SECRET, REFRESH_SECRET, 3600L, 7200L);

        assertThatThrownBy(() -> jwtTokenProvider.getEmail("invalid.token.value"))
                .isInstanceOf(RuntimeException.class);
    }

    // Refresh Token마다 재사용 방지용 난수 jti를 포함하는지 검증한다.
    @Test
    void generateRefreshToken_includesDistinctJti() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(ACCESS_SECRET, REFRESH_SECRET, 3600L, 7200L);

        String firstToken = jwtTokenProvider.generateRefreshToken();
        String secondToken = jwtTokenProvider.generateRefreshToken();
        Claims firstClaims = Jwts.parser().verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode(REFRESH_SECRET)
        )).build().parseSignedClaims(firstToken).getPayload();
        Claims secondClaims = Jwts.parser().verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode(REFRESH_SECRET)
        )).build().parseSignedClaims(secondToken).getPayload();

        assertThat(firstClaims.getId()).isNotBlank();
        assertThat(secondClaims.getId()).isNotBlank();
        assertThat(firstClaims.getId()).isNotEqualTo(secondClaims.getId());
    }
}
