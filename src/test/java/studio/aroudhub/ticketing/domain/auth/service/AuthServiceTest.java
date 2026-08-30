package studio.aroudhub.ticketing.domain.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.RefreshTokenRepository;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.LoginRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.CheckResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.entity.RefreshToken;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;
import studio.aroudhub.ticketing.domain.auth.repository.entity.UserRole;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;

import java.util.Optional;
import java.util.Date;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    // 유효한 Access Token의 사용자 정보를 반환한다.
    void check_whenTokenIsValidAndUserExists_returnsUserInfo() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        User user = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER);

        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("alice@example.com");
        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        CheckResponse response = authService.check("valid-token");

        assertThat(response).isEqualTo(new CheckResponse("Alice", "alice@example.com"));
    }

    @Test
    // 만료된 Access Token은 사용자 조회 전에 접근을 거부한다.
    void check_whenTokenIsExpired_throwsUnauthorized() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);

        when(jwtTokenProvider.isAccessTokenExpired("expired-token")).thenReturn(true);

        assertThatThrownBy(() -> authService.check("expired-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verifyNoInteractions(authRepository);
    }

    @Test
    // 위조되었거나 형식이 잘못된 Access Token은 접근을 거부한다.
    void check_whenTokenIsInvalid_throwsUnauthorized() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);

        when(jwtTokenProvider.isAccessTokenExpired("invalid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("invalid-token")).thenThrow(new IllegalArgumentException("유효하지 않은 토큰입니다."));

        assertThatThrownBy(() -> authService.check("invalid-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verifyNoInteractions(authRepository);
    }

    @Test
    // 삭제되었거나 존재하지 않는 인증 사용자는 접근을 거부한다.
    void check_whenUserDoesNotExist_throwsUnauthorized() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);

        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("alice@example.com");
        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.check("valid-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void signup_encodesPasswordAndSavesUser() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        SignupRequest request = new SignupRequest("  Alice  ", "  alice@example.com  ", "  plain-password  ", "  010-1111-2222  ");

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");

        authService.signup(request);

        verify(authRepository).findByEmail("alice@example.com");
        verify(passwordEncoder).encode("plain-password");
        verify(authRepository).save(any(User.class));
        verify(authRepository).flush();
    }

    @Test
    void signup_whenEmailAlreadyExists_throwsConflict() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        SignupRequest request = new SignupRequest("Alice", "alice@example.com", "plain-password", "010-1111-2222");

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(mock(User.class)));

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(authRepository).findByEmail("alice@example.com");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void signup_whenUniqueConstraintFails_throwsConflict() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        SignupRequest request = new SignupRequest("Alice", "alice@example.com", "plain-password", "010-1111-2222");

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        doThrow(new DataIntegrityViolationException("duplicate key"))
                .when(authRepository)
                .flush();

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_whenEmailDoesNotExist_throwsUnauthorized() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        LoginRequest request = new LoginRequest(" alice@example.com ", " plain-password ");

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(authRepository).findByEmail("alice@example.com");
        verifyNoInteractions(passwordEncoder, jwtTokenProvider);
    }

    @Test
    void login_whenPasswordDoesNotMatch_throwsUnauthorized() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        LoginRequest request = new LoginRequest("alice@example.com", "plain-password");
        User savedUser = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER);

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(passwordEncoder).matches("plain-password", "encoded-password");
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    // ?類ㅺ맒 嚥≪뮄?????Access Token??Refresh Token????ｍ뜞 獄쏆뮄???랁????館釉??
    void login_whenCredentialsValid_returnsAccessTokenAndStoresRefreshToken() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        LoginRequest request = new LoginRequest(" alice@example.com ", " plain-password ");
        User savedUser = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER);

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(" plain-password ", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken("alice@example.com")).thenReturn("jwt-token");
        when(jwtTokenProvider.generateRefreshToken()).thenReturn("refresh-token");
        when(jwtTokenProvider.getRefreshTokenExpiration("refresh-token"))
                .thenReturn(new Date(1_900_000_000_000L));

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        verify(passwordEncoder).matches(" plain-password ", "encoded-password");
        verify(jwtTokenProvider).generateAccessToken("alice@example.com");
        verify(refreshTokenRepository).save(any());
    }

    // 로그아웃은 Access Token을 서버에서 무효화하지 않고 Refresh Token만 모두 폐기한다.
    @Test
    void logout_deletesOnlyUsersRefreshTokens() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        User user = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER);

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        authService.logout("alice@example.com");

        verify(refreshTokenRepository).deleteAllByUser(user);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    // ??λ맂 Refresh Token? ?뚯쟾 ?????좏겙?쇰줈 援먯껜?쒕떎.
    void refresh_whenStoredTokenIsValid_rotatesToken() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        User user = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER);
        RefreshToken storedToken = new RefreshToken(user, "stored-token-hash", LocalDateTime.now().plusDays(1));

        when(jwtTokenProvider.getRefreshTokenExpiration("refresh-token"))
                .thenReturn(new Date(1_900_000_000_000L));
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(storedToken));
        when(jwtTokenProvider.generateAccessToken("alice@example.com")).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken()).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getRefreshTokenExpiration("new-refresh-token"))
                .thenReturn(new Date(1_900_000_000_000L));
        when(refreshTokenRepository.deleteIfUsable(
                eq(storedToken.getRefreshId()), anyString(), any(LocalDateTime.class)
        )).thenReturn(1);

        LoginResponse response = authService.refresh("refresh-token");

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        verify(refreshTokenRepository).deleteIfUsable(
                eq(storedToken.getRefreshId()), anyString(), any(LocalDateTime.class)
        );
        verify(refreshTokenRepository).save(any());
    }

    @Test
    // 이미 소비된 Refresh Token은 새 토큰을 발급하지 않는다.
    void refresh_whenStoredTokenWasAlreadyConsumed_throwsUnauthorized() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider, refreshTokenRepository);
        User user = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER);
        RefreshToken storedToken = new RefreshToken(user, "stored-token-hash", LocalDateTime.now().plusDays(1));

        when(jwtTokenProvider.getRefreshTokenExpiration("refresh-token"))
                .thenReturn(new Date(1_900_000_000_000L));
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(storedToken));
        when(refreshTokenRepository.deleteIfUsable(
                eq(storedToken.getRefreshId()), anyString(), any(LocalDateTime.class)
        )).thenReturn(0);

        assertThatThrownBy(() -> authService.refresh("refresh-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(refreshTokenRepository).deleteIfUsable(
                eq(storedToken.getRefreshId()), anyString(), any(LocalDateTime.class)
        );
        verifyNoInteractions(authRepository);
    }
}
