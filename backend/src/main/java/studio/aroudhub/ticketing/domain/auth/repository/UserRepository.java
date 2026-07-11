package studio.aroudhub.ticketing.domain.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    // 로그인 시 이메일로 회원 정보를 조회한다.
    Optional<Users> findByEmail(String email);

    // 회원가입 시 같은 이메일이 이미 존재하는지 확인한다.
    boolean existsByEmail(String email);
}
