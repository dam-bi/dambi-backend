package studio.aroudhub.ticketing.domain.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.LoginRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.SignupResponse;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
    * POST /api/auth/login
    * 로그인 요청, DB 조회로 로그인 체크, 로그인 시 케이스에 따라 jwt 토큰 생성 및 대조
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        authService.login(loginRequest);
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /*
     * POST /api/auth/signup
     * 회원가입 기능.
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupReq) {
        authService.signup(signupReq);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignupResponse("회원가입이 완료되었습니다."));
    }

    /*
     * POST /api/auth/logout
     * 로그아웃 기능
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("TODO: 로그아웃 처리");
    }

    /*
    * POST /api/auth/refresh
    * 목적: JWT refresh token은 유효하지만 access token 만료 시, JWT access token 갱신용
     */
    @PostMapping("/refresh")
    public void refresh(){
        authService.refresh();
    }
}
