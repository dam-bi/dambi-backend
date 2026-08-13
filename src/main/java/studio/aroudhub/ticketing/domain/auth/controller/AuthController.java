package studio.aroudhub.ticketing.domain.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.SignupResponse;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    // 로그인 엔드포인트
    // POST /api/auth/login
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        return ResponseEntity.ok("TODO: 로그인 처리");
    }

    @PostMapping("/signup")
    // 회원가입 엔드포인트
    // POST /api/auth/signup
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupReq) {
        authService.signup(signupReq);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignupResponse("회원가입이 완료되었습니다."));
    }

    @PostMapping("/logout")
    // 로그아웃 엔드포인트
    // POST /api/auth/logout
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("TODO: 로그아웃 처리");
    }
}
