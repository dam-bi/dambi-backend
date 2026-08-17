package studio.aroudhub.ticketing.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Override
    Optional<RefreshToken> findById(Long aLong);
}
