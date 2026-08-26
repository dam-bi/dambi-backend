package studio.aroudhub.ticketing.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import studio.aroudhub.ticketing.domain.auth.repository.entity.RefreshToken;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    // 유효한 Refresh Token을 한 번만 소비하도록 조건부로 삭제한다.
    @Modifying
    @Query("""
            DELETE FROM RefreshToken refreshToken
            WHERE refreshToken.refreshId = :refreshId
              AND refreshToken.token = :token
              AND refreshToken.expiredAt > :now
            """)
    int deleteIfUsable(
            @Param("refreshId") Long refreshId,
            @Param("token") String token,
            @Param("now") LocalDateTime now
    );

    /*
    * 호출: logout 시에 호출
    * 기능: 지정한 사용자의 refresh token 이력을 table에서 삭제
     */
    void deleteAllByUser(User user);
}
