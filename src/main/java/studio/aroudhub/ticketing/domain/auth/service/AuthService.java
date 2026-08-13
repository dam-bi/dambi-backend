package studio.aroudhub.ticketing.domain.auth.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.DTO.request.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void login() {
        //
    }

    @Transactional
    public void signup(SignupRequest req) {
        String userName = req.name().trim();
        String email = req.email().trim();
        String rawPassword = req.password().trim();
        String phoneNumber = req.phoneNumber().trim();

        // 같은 이메일이 이미 존재하면 회원가입을 막는다.
        Optional<User> checkUser = authRepository.findByEmail(email);
        if (checkUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 계정입니다.");
        }

        // 프론트에서 받은 평문 비밀번호를 암호화해서 DB에 저장한다.
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
