package studio.aroudhub.ticketing.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.aroudhub.ticketing.domain.auth.repository.UserRepository;
import studio.aroudhub.ticketing.domain.auth.repository.dto.SignupRequest;
import studio.aroudhub.ticketing.domain.auth.repository.entity.Users;

@Service
@RequiredArgsConstructor
@Profile({"local", "dev", "prod"})
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 이메일이 존재합니다.");
        }

        Users user = new Users(
                signupRequest.getName(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getEmail(),
                signupRequest.getPhoneNumber()
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean login(String email, String password) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일이나 패스워드가 일치하지 않습니다."));

        return passwordEncoder.matches(password, user.getPassword());
    }
}
