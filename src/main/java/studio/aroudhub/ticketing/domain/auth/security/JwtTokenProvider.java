package studio.aroudhub.ticketing.domain.auth.security;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
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

    private final SecretKey secretKey;
    private final long accessTokenExpSec;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-seconds}") long accessTokenExpSec
    ) {
        // 설정값으로 받은 시크릿 문자열을 JWT 서명에 사용할 키로 변환한다.
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); // base64 디코딩
        this.accessTokenExpSec = accessTokenExpSec;
    }

    // token 생성. access token
    public String generateToken(String email) {
        // 현재 시각을 기준으로 만료 시간이 있는 access token을 생성한다.
        Instant now = Instant.now();

        return Jwts.builder()
                .claim("email", email) // private claim
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpSec)))
                .signWith(secretKey)
                .compact();
    }

    // jwt token 만료 여부 확인
    public boolean isTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(Date.from(Instant.now()));
    }

    // jwt에서 이메일 추출. filter에서 로그인 검증용으로 사용할 예정.
    public String getEmail(String token){
            return Jwts.parser().
                    verifyWith(secretKey)
                    .build().parseSignedClaims(token) // parseSignedClaims : jwt 형식, 서명 위조 여부, 만료여부, 키 일치 여부 검사
                    .getPayload()
                    .get("email", String.class);

    }

    // refresh token 제작 메서드 기능구현 예정
}
