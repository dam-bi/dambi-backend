package studio.aroudhub.ticketing.domain.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.CheckResponse;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    // 유효한 Access Token으로 사용자 정보를 반환한다.
    void check_whenTokenIsValid_returnsUserInfo() throws Exception {
        when(authService.check("valid-token"))
                .thenReturn(new CheckResponse("Alice", "alice@example.com"));

        mockMvc.perform(post("/api/auth/check")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.phone").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());

        verify(authService).check("valid-token");
    }

    @Test
    // 토큰의 사용자가 삭제된 경우 인증 실패를 반환한다.
    void check_whenUserDoesNotExist_returnsUnauthorized() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "지정한 사용자를 찾을 수 없습니다."))
                .when(authService)
                .check("valid-token");

        mockMvc.perform(post("/api/auth/check")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signup_whenRequestValid_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alice",
                                  "email": "alice@example.com",
                                  "password": "plain-password",
                                  "phone": "010-1111-2222"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(authService).signup(any());
    }

    @Test
    void signup_whenEmailBlank_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alice",
                                  "email": "   ",
                                  "password": "plain-password",
                                  "phone": "010-1111-2222"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void signup_whenDuplicateEmail_returnsConflict() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "?먮윭諛쒖깮"))
                .when(authService)
                .signup(any());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alice",
                                  "email": "alice@example.com",
                                  "password": "plain-password",
                                  "phone": "010-1111-2222"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    // 로그인 응답에 Refresh Token 쿠키를 포함한다.
    void login_whenRequestValid_returnsOk() throws Exception {
        org.mockito.Mockito.when(authService.login(any()))
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
    // 濡쒓렇???낅젰 寃利앹쓣 frontend?먯꽌 ?섑뻾?섎?濡??붿껌??service???꾨떖?쒕떎.
    // 로그인 입력은 서비스 계층으로 전달한다.
    void login_whenEmailBlank_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "   ",
                                  "password": "plain-password"
                                }
                                """))
                .andExpect(status().isBadRequest());

        org.mockito.Mockito.verifyNoInteractions(authService);
    }

    // 로그인 이메일 형식이 잘못되면 서비스 호출 전에 400을 반환한다.
    @Test
    void login_whenEmailMalformed_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": "plain-password"
                                }
                                """))
                .andExpect(status().isBadRequest());

        org.mockito.Mockito.verifyNoInteractions(authService);
    }

    // 로그인 비밀번호가 공백이면 서비스 호출 전에 400을 반환한다.
    @Test
    void login_whenPasswordBlank_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@example.com",
                                  "password": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());

        org.mockito.Mockito.verifyNoInteractions(authService);
    }

    @Test
    // 인증 실패한 재발급 요청은 Refresh Token 쿠키를 만료시킨다.
    void refresh_whenTokenIsInvalid_returnsUnauthorizedAndClearsCookie() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이미 사용된 Refresh Token입니다."))
                .when(authService)
                .refresh("refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "refresh-token")))
                .andExpect(status().isUnauthorized())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));
    }

    @Test
    // 재발급 중 서버 오류는 인증 실패로 바꾸지 않는다.
    void refresh_whenUnexpectedErrorOccurs_returnsServerError() throws Exception {
        doThrow(new IllegalStateException("database unavailable"))
                .when(authService)
                .refresh("refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "refresh-token")))
                .andExpect(status().isInternalServerError());
    }

}
