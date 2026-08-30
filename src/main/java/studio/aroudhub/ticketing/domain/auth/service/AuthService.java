package studio.aroudhub.ticketing.domain.auth.service;

import jakarta.transaction.Transactional;
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
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.CheckResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.LoginResponse;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.response.UserInfo;
import studio.aroudhub.ticketing.domain.auth.repository.RefreshTokenRepository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.RefreshToken;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;
import studio.aroudhub.ticketing.domain.auth.repository.entity.UserRole;
import studio.aroudhub.ticketing.domain.auth.security.JwtTokenProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Locale;

@Service
public class AuthService {

    private static final Logger log = LogManager.getLogger();
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 인증에 필요한 저장소와 토큰 발급기를 주입한다.
    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 이메일과 비밀번호를 검증하고 Access/Refresh Token을 발급한다.
    @Transactional
    public LoginResponse login(LoginRequest req) {

        String email = req.email().trim();
        String rawPassword = req.password();
        // email 검증
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "가입되지 않았거나 이메일이 올바르지 않습니다."));
        // pw 검증
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        }

        return createTokens(user);
    }

    // Refresh Token을 검증하고 기존 토큰을 교체해 새 Access Token을 발급한다.
    @Transactional
    public LoginResponse refresh(String rowRefreshToken) {
        // 토큰값 null/blank 검사
        if (rowRefreshToken == null || rowRefreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다.");
        }

        // 토큰이 유효한지 검사
        try {
            jwtTokenProvider.getRefreshTokenExpiration(rowRefreshToken);
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        String tokenHash = hashToken(rowRefreshToken);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."));
        if (refreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 Refresh Token입니다.");
        }

        // 회전 전 토큰을 먼저 삭제해 재사용을 차단한다.
        int deletedCount = refreshTokenRepository.deleteIfUsable(
                refreshToken.getRefreshId(), tokenHash, LocalDateTime.now()
        );
        if (deletedCount != 1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이미 사용되었거나 만료된 Refresh Token입니다.");
        }
        return createTokens(refreshToken.getUser());
    }

    // 가입 요청을 검증하고 비밀번호를 암호화해 사용자를 저장한다.
    @Transactional
    public void signup(SignupRequest req) {
        UserRole role = parseUserRole(req.role());
        String userName = req.name().trim();
        String email = req.email().trim();
        String rawPassword = req.password().trim();
        String phoneNumber = req.phone().trim();

        if (authRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 계정입니다.");
        }

        try {
            authRepository.save(new User(userName, email, passwordEncoder.encode(rawPassword), phoneNumber, role));
            authRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            // 데이터 무결성 위반
            log.warn("중복 이메일 가입 요청: {}", email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 계정입니다.");
        }
    }

    // 대소문자를 구분하지 않고 가입 역할을 enum으로 변환한다.
    private UserRole parseUserRole(String role) {
        if (role == null || role.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "역할은 필수입니다.");
        }

        try {
            return UserRole.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 role입니다.");
        }
    }

    // 인증된 사용자의 Refresh Token을 모두 제거해 모든 세션을 로그아웃한다.
    @Transactional
    public void logout(String email) {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "지정한 사용자를 찾을 수 없습니다."));
        // refreshToken Table에서 삭제
        refreshTokenRepository.deleteAllByUser(user);
    }

    // Access Token을 검증하고 현재 사용자 정보를 반환한다.
    public CheckResponse check(String token) {
        // access token이 만료되었는지 확인.
        if (jwtTokenProvider.isAccessTokenExpired(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 Access Token 입니다.");
        }

        String email = jwtTokenProvider.getEmail(token);
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "지정한 사용자를 찾을 수 없습니다."));
        return new CheckResponse(user.getName(), user.getEmail(), user.getRole());
    }

    // 사용자용 Access Token과 DB에 저장할 Refresh Token을 함께 발급한다.
    private LoginResponse createTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken();
        LocalDateTime expiredAt = LocalDateTime.ofInstant(
                jwtTokenProvider.getRefreshTokenExpiration(rawRefreshToken).toInstant(), ZoneId.systemDefault());

        // refreshToken Table에 insert
        refreshTokenRepository.save(new RefreshToken(user, hashToken(rawRefreshToken), expiredAt));
        return new LoginResponse(accessToken, new UserInfo(user.getName(), user.getEmail(), user.getPhone(), user.getRole()), rawRefreshToken);
    }

    // Refresh Token 원문을 저장하지 않도록 SHA-256 해시를 만든다.
    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("해당 방식을 사용할 수 없습니다.", exception);
        }
    }
}
