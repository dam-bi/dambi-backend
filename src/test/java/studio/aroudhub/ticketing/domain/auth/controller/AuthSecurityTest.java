package studio.aroudhub.ticketing.domain.auth.controller;

import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;
import studio.aroudhub.ticketing.config.SecurityConfig;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;
import studio.aroudhub.ticketing.domain.auth.repository.entity.UserRole;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.CheckResponse;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, AdminEndpointTestConfig.class})
class AuthSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AuthRepository authRepository;

    @Test
    // Access Token이 없으면 사용자 정보 확인을 거부한다.
    void check_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/check"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    // 만료된 Access Token으로는 사용자 정보를 조회할 수 없다.
    void check_withExpiredToken_isUnauthorized() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("expired-token")).thenReturn(true);

        mockMvc.perform(post("/api/auth/check")
                        .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    // 위조되었거나 형식이 잘못된 Access Token으로는 사용자 정보를 조회할 수 없다.
    void check_withInvalidToken_isUnauthorized() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("invalid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("invalid-token")).thenThrow(new MalformedJwtException("invalid token"));

        mockMvc.perform(post("/api/auth/check")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    // 유효한 Access Token으로는 사용자 정보를 반환한다.
    void check_withValidToken_returnsUserInfo() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("alice@example.com");
        when(authRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER)));
        when(authService.check("valid-token"))
                .thenReturn(new CheckResponse("Alice", "alice@example.com", UserRole.USER));

        mockMvc.perform(post("/api/auth/check")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    // Access Token 없이도 로그인 요청은 허용된다.
    void login_withoutToken_isPermitted() throws Exception {
        when(authService.login(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse("access-token", null, "refresh-token"));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@example.com",
                                  "password": "plain-password"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void logout_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    // Refresh Token 쿠키만으로는 재발급 요청을 수행할 수 없다.
    // CSRF 보호를 사용하지 않는 Refresh Token 재발급 요청을 허용한다.
    void refresh_withoutCsrfToken_isPermitted() throws Exception {
        when(authService.refresh("refresh-token"))
                .thenReturn(new studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse(
                        "access-token", null, "new-refresh-token"
                ));

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "refresh-token")))
                .andExpect(status().isOk());
    }

    // 유효한 Access Token으로 로그아웃 요청을 허용한다.
    @Test
    // 留뚮즺?섏? ?딆? token?쇰줈 蹂댄샇??濡쒓렇?꾩썐 endpoint???묎렐?쒕떎.
    void logout_withValidToken_isPermitted() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("alice@example.com");
        when(authRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(new User("Alice", "alice@example.com", "encoded-password", "010-1111-2222", UserRole.USER)));

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    // 토큰 없이 관리자 endpoint에 접근하면 인증을 거부한다.
    void adminEndpoint_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    // USER 역할은 관리자 endpoint에 접근할 수 없다.
    void adminEndpoint_withUserRole_isForbidden() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("user-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("user-token")).thenReturn("user@example.com");
        when(authRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(new User("User", "user@example.com", "encoded-password", "010-1111-2222", UserRole.USER)));

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    // ADMIN 역할은 관리자 endpoint에 접근할 수 있다.
    void adminEndpoint_withAdminRole_isPermitted() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("admin-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("admin-token")).thenReturn("admin@example.com");
        when(authRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(new User("Admin", "admin@example.com", "encoded-password", "010-1234-5678", UserRole.ADMIN)));

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk());
    }

    @Test
    // ?섎せ??token? 401 ?묐떟??諛섑솚?쒕떎.
    // 잘못된 JWT가 포함된 로그인 요청은 인증 실패로 처리한다.
    void login_withInvalidToken_isUnauthorized() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("invalid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("invalid-token")).thenThrow(new MalformedJwtException("invalid token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@example.com",
                                  "password": "plain-password"
                                }
                                """)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authService);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class AdminEndpointTestConfig {

        // 관리자 인가 규칙을 검증할 테스트 전용 endpoint를 등록한다.
        @Bean
        TestAdminController testAdminController() {
            return new TestAdminController();
        }
    }

    @RestController
    static class TestAdminController {

        // 관리자 인가 규칙 검증용 응답을 반환한다.
        @GetMapping("/api/admin/test")
        String getAdminEndpoint() {
            return "ok";
        }
    }
}
