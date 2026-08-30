package studio.aroudhub.ticketing.domain.auth.repository.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserTest {

    // 사용자 생성 시 지정한 역할을 보존한다.
    @Test
    void preservesAssignedRole() {
        User user = new User(
                "Alice",
                "alice@example.com",
                "encoded-password",
                "010-1111-2222",
                UserRole.ADMIN
        );

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }
}
