package studio.aroudhub.ticketing.domain.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.LoginRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.UserInfo;
import studio.aroudhub.ticketing.domain.auth.repository.RefreshTokenRepository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.RefreshToken;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LogManager.getLogger();
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /*
    * 로그인 처리 메서드
    * POST /api/auth/login
    * 기능
    *
     */
    @Transactional
    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim();
        String rawPassword = req.password().trim();

        // DB에 일치하는 이메일 있는지 검사.
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "가입되지 않았거나 틀린 이메일입니다"));

        // PW가 일치하는지 검사
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "패스워드가 틀렸습니다");
        }

        // jwt access token, refresh token 발급
        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        Date refreshTokenExpireDate = jwtTokenProvider.getExpiredTimeFromRefreshToken(refreshToken);

        // 발급한 refresh token을 DB에 저장
        RefreshToken refreshTokenEntity = new RefreshToken(user.getUsersId(), refreshToken, refreshTokenExpireDate);
        this.saveRefreshToken(refreshTokenEntity);

        // loginResponse 생성
        UserInfo userInfo = new UserInfo(user.getName(), user.getEmail(), user.getPhone());
        return new LoginResponse(accessToken, userInfo);
    }

    /*
    * 발급한 refresh token hash, user table user_id을 db에 저장
     */
    @Transactional
    private void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    // refresh token 유효한 상황에서 accessToken 재발급
    private void reissueAccessToken(String refreshToken){
        // 1. refresh token 유효한지 검증
        if(jwtTokenProvider.isRefreshTokenExpired(refreshToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
        }

        // 2. 토큰에서 email 가져오기
        // 3. DB에서 email 기반으로 refresh token 값 가져옴
        // 4. refresh token 검사

    }

    // 회원가입.
    @Transactional
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

    /*
     * POST /api/auth/logout
     * 기능
     * refresh token, access token 폐기
     */
    @Transactional
    public void logout() {
    }

    /*
    * POST /api/auth/refresh
     */
    @Transactional
    public void refresh(){

    }
}
