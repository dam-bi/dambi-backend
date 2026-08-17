package studio.aroudhub.ticketing.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
