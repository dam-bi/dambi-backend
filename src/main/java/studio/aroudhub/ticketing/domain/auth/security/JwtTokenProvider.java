package studio.aroudhub.ticketing.domain.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenProvider {

    private static final String ACCESS_TOKEN_SUBJECT = "accessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "refreshToken";
    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;
    private final long accessTokenExpSec;
    private final long refreshTokenExpSec;

    // Access Token과 Refresh Token의 서명 키 및 만료 시간을 설정한다.
    public JwtTokenProvider(
            @Value("${jwt.access-secret}") String accessSecretKey,
            @Value("${jwt.refresh-secret}") String refreshSecretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpSec,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpSec
    ) {
        this.accessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKey));
        this.refreshSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKey));
        this.accessTokenExpSec = accessTokenExpSec;
        this.refreshTokenExpSec = refreshTokenExpSec;
    }

    // 이메일을 담은 Access Token을 발급한다.
    public String generateAccessToken(String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(ACCESS_TOKEN_SUBJECT)
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpSec)))
                .signWith(accessSecretKey)
                .compact();
    }

    // 세션 갱신 전용 Refresh Token을 발급한다.
    public String generateRefreshToken() {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(REFRESH_TOKEN_SUBJECT)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTokenExpSec)))
                .signWith(refreshSecretKey)
                .compact();
    }

    // Refresh Token의 서명과 용도를 검증하고 만료 시각을 반환한다.
    public Date getRefreshTokenExpiration(String token) {
        Claims claims = Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload();
        if (!REFRESH_TOKEN_SUBJECT.equals(claims.getSubject())) {
            throw new IllegalArgumentException("Refresh Token 용도가 올바르지 않습니다.");
        }
        return claims.getExpiration();
    }

    // Access Token의 만료 여부를 확인한다.
    public boolean isAccessTokenExpired(String token) {
        return Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token)
                .getPayload().getExpiration().before(Date.from(Instant.now()));
    }

    // Access Token에서 인증 사용자 이메일을 추출한다.
    public String getEmail(String token) {
        Claims claims = Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token).getPayload();
        if (!ACCESS_TOKEN_SUBJECT.equals(claims.getSubject())) {
            throw new IllegalArgumentException("Access Token 용도가 올바르지 않습니다.");
        }
        return claims.get("email", String.class);
    }
}
