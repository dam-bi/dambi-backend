package studio.aroudhub.ticketing.domain.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenProvider {

    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;
    private final long accessTokenExpSec;
    private final long refreshTokenExpSec;

    // 생성자
    public JwtTokenProvider(
            @Value("${jwt.access-secret}") String accessSecretKey,
            @Value("${jwt.refresh-secret}") String refreshSecretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpSec,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpSec
    ) {
        // 설정값으로 받은 시크릿 문자열을 JWT 서명에 사용할 키로 변환한다.
        this.accessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKey)); // base64 디코딩
        this.refreshSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKey));
        this.accessTokenExpSec = accessTokenExpSec;
        this.refreshTokenExpSec = refreshTokenExpSec;
    }

    // access token 발급.
    public String generateAccessToken(String email) {
        // 현재 시각을 기준으로 만료 시간이 있는 access token을 생성한다.
        Instant now = Instant.now();

        return Jwts.builder()
                .subject("accessToken")
                .claim("email", email) // private claim. 로그인 email값
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpSec)))
                .signWith(accessSecretKey)
                .compact();
    }

    // refresh token 발급.
    public String generateRefreshToken() {
        // 현재 시간
        Instant now = Instant.now();

        return Jwts.builder()
                .subject("refreshToken")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTokenExpSec)))
                .signWith(refreshSecretKey)
                .compact();
    }

    //refresh token 만료시간 반환(DB filed type: timezone)
    public Date getExpiredTimeFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build().parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    // access, refresh 동시 발급 메서드 생성 필요한지 고민 중.

    // jwt access token 만료 여부 확인. True: 만료. False: 유효
    public boolean isAccessTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(accessSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(Date.from(Instant.now()));
    }

    // jwt refresh token 만료 여부 확인. True: 만료. False: 유효
    public boolean isRefreshTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(Date.from(Instant.now()));
    }

    // jwt에서 이메일 추출. filter에서 로그인 검증용으로 사용할 예정.
    public String getEmail(String token){
            return Jwts.parser().
                    verifyWith(accessSecretKey)
                    .build().parseSignedClaims(token) // parseSignedClaims : jwt 형식, 서명 위조 여부, 만료여부, 키 일치 여부 검사
                    .getPayload()
                    .get("email", String.class);
    }
}
