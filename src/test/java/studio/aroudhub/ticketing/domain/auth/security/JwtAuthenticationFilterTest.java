package studio.aroudhub.ticketing.domain.auth.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;
import studio.aroudhub.ticketing.domain.auth.repository.entity.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @AfterEach
    // 테스트 간 인증 상태가 공유되지 않도록 초기화한다.
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    // 유효한 JWT의 사용자 역할을 Spring Security authority로 복원한다.
    void validToken_restoresUserRoleAuthority() throws Exception {
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthRepository authRepository = mock(AuthRepository.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("admin@example.com");
        when(authRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(new User("Admin", "admin@example.com", "encoded-password", "010-1234-5678", UserRole.ADMIN)));

        jwtAuthenticationFilter.doFilterInternal(request, response, (servletRequest, servletResponse) -> { });

        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    // 유효한 USER JWT의 역할을 ROLE_USER authority로 복원한다.
    void validUserToken_restoresUserAuthority() throws Exception {
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthRepository authRepository = mock(AuthRepository.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("user@example.com");
        when(authRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(new User("User", "user@example.com", "encoded-password", "010-1234-5678", UserRole.USER)));

        jwtAuthenticationFilter.doFilterInternal(request, response, (servletRequest, servletResponse) -> { });

        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    // 토큰 사용자가 없으면 인증 정보를 만들지 않고 401을 반환한다.
    void validToken_whenUserDoesNotExist_isUnauthorizedWithoutAuthentication() throws Exception {
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthRepository authRepository = mock(AuthRepository.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("deleted@example.com");
        when(authRepository.findByEmail("deleted@example.com")).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilterInternal(request, response, (servletRequest, servletResponse) -> { });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    // 형식이 잘못된 Authorization 헤더는 인증 실패로 거부한다.
    void malformedAuthorizationHeader_isUnauthorized() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                mock(JwtTokenProvider.class), mock(AuthRepository.class)
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "invalid-token");

        jwtAuthenticationFilter.doFilterInternal(request, response, (servletRequest, servletResponse) -> { });

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    // 토큰 검증 실패 시 사용자 인증 정보는 조회하지 않는다.
    void tokenVerificationFailure_doesNotQueryUser() throws Exception {
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthRepository authRepository = mock(AuthRepository.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer invalid-token");

        when(jwtTokenProvider.isAccessTokenExpired("invalid-token"))
                .thenThrow(new MalformedJwtException("invalid token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, (servletRequest, servletResponse) -> { });

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(authRepository);
    }

    @Test
    // 사용자 인증 정보 조회 실패는 인증 실패로 변환하지 않고 전파한다.
    void userLookupFailure_isPropagated() {
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthRepository authRepository = mock(AuthRepository.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("admin@example.com");
        when(authRepository.findByEmail("admin@example.com"))
                .thenThrow(new DataAccessResourceFailureException("database unavailable"));

        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(
                request, response, (servletRequest, servletResponse) -> { }
        )).isInstanceOf(DataAccessResourceFailureException.class);
    }
}
