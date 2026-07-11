package studio.aroudhub.ticketing.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.auth.repository.dto.AuthResponse;
import studio.aroudhub.ticketing.domain.auth.repository.dto.LoginRequest;
import studio.aroudhub.ticketing.domain.auth.repository.dto.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Profile({"local", "dev", "prod"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest signupRequest) {
        authService.signUp(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(true, "회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        boolean authenticated = authService.login(loginRequest.email(), loginRequest.password());
        if (!authenticated) {
            throw new IllegalArgumentException("0이메일이나 패스워드 입력이 잘못되었습니다.");
        }

        return ResponseEntity.ok(new AuthResponse(true, "로그인이 완료되었습니다."));
    }
}
