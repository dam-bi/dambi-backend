package studio.aroudhub.ticketing.domain.auth.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.LoginRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.UserInfo;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LogManager.getLogger();
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /*
    * 로그인 처리 메서드
    * POST /api/auth/login
     */
    @Transactional
    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim();
        String rawPassword = req.password().trim();

        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "가입되지 않았거나 틀린 이메일입니다"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "패스워드가 틀렸습니다");
        }

        // jwt access token 발급
        String accessToken = jwtTokenProvider.generateToken(email);

        UserInfo userInfo = new UserInfo(user.getName(), user.getEmail(), user.getPhone());
        return new LoginResponse(accessToken, userInfo);
    }

    @Transactional
    // 회원가입.
    public void signup(SignupRequest req) {
        String userName = req.name().trim();
        String email = req.email().trim();
        String rawPassword = req.password().trim();
        String phoneNumber = req.phone().trim();

        Optional<User> checkUser = authRepository.findByEmail(email);
        if (checkUser.isPresent()) {
            log.warn("중복된 이메일입니다: {}", email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 계정입니다.");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(userName, email, encodedPassword, phoneNumber);

        try {
            authRepository.save(user);
            authRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 계정입니다.");
        }
    }

    @Transactional
    public void logout() {
    }


}
