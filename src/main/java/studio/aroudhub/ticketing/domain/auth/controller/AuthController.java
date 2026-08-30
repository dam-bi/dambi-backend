package studio.aroudhub.ticketing.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.LoginRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.CheckResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.SignupResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.UserInfo;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean secureCookie;
    private final long refreshTokenExpirationSeconds;

    // String 상수
    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String BEARER_PREFIX = "Bearer ";

    // 인증 서비스와 Refresh Token 쿠키 설정을 주입한다.
    public AuthController(
            AuthService authService,
            @Value("${auth.cookie.secure:false}") boolean secureCookie,
            @Value("${jwt.refresh-token-expiration:10000}") long refreshTokenExpirationSeconds
    ) {
        this.authService = authService;
        this.secureCookie = secureCookie;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

    // 로그인 성공 시 Access Token 응답과 HttpOnly Refresh Token 쿠키를 반환한다.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse result = authService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshCookie(result.refreshToken()).toString())
                .body(result);
    }

    // 회원가입 요청을 처리한다.
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupReq) {
        authService.signup(signupReq);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignupResponse("회원가입이 완료되었습니다."));
    }

    // 유효한 Access Token의 사용자 정보를 반환한다. 유효하지 않으면 401 에러를 반환한다.
    @PostMapping("/check")
    public ResponseEntity<CheckResponse> check(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        String accessToken = header.substring(BEARER_PREFIX.length()).trim();
        return ResponseEntity.ok(authService.check(accessToken));
    }

    // 인증된 사용자의 모든 Refresh Token을 폐기하고 쿠키를 제거한다.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString()).build();
    }

    // Refresh Token 쿠키로 Access Token과 Refresh Token을 교체 발급한다.
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        try {
            LoginResponse result = authService.refresh(refreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, createRefreshCookie(result.refreshToken()).toString())
                    .body(result);
        } catch (org.springframework.web.server.ResponseStatusException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
                    .build();
        }
    }

    // Refresh Token 쿠키를 현재 보안 정책에 맞춰 생성한다.
    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofSeconds(refreshTokenExpirationSeconds))
                .build();
    }

    // 브라우저에 남은 Refresh Token 쿠키를 즉시 만료시킨다.
    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}
