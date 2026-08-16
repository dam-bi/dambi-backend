package studio.aroudhub.ticketing.domain.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.LoginRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    void signup_encodesPasswordAndSavesUser() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider);
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
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider);
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
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider);
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
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider);
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
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider);
        LoginRequest request = new LoginRequest("alice@example.com", "plain-password");
        User savedUser = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222");

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
    void login_whenCredentialsValid_returnsAccessToken() {
        AuthRepository authRepository = mock(AuthRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthService authService = new AuthService(authRepository, passwordEncoder, jwtTokenProvider);
        LoginRequest request = new LoginRequest(" alice@example.com ", " plain-password ");
        User savedUser = new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222");

        when(authRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("alice@example.com")).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        verify(jwtTokenProvider).generateToken("alice@example.com");
    }
}
