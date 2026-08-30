package studio.aroudhub.ticketing.domain.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;
import studio.aroudhub.ticketing.config.SecurityConfig;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.CheckResponse;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

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
        when(jwtTokenProvider.getEmail("invalid-token")).thenThrow(new RuntimeException("invalid token"));

        mockMvc.perform(post("/api/auth/check")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    // 유효한 Access Token으로는 사용자 정보를 반환한다.
    void check_withValidToken_returnsUserInfo() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("alice@example.com");
        when(authService.check("valid-token"))
                .thenReturn(new CheckResponse("Alice", "alice@example.com"));

        mockMvc.perform(post("/api/auth/check")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
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

    @Test
    // 留뚮즺?섏? ?딆? token?쇰줈 蹂댄샇??濡쒓렇?꾩썐 endpoint???묎렐?쒕떎.
    void logout_withValidToken_isPermitted() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("valid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("valid-token")).thenReturn("alice@example.com");

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    // ?섎せ??token? 401 ?묐떟??諛섑솚?쒕떎.
    void login_withInvalidToken_isUnauthorized() throws Exception {
        when(jwtTokenProvider.isAccessTokenExpired("invalid-token")).thenReturn(false);
        when(jwtTokenProvider.getEmail("invalid-token")).thenThrow(new RuntimeException("invalid token"));

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
}
