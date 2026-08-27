package studio.aroudhub.ticketing.domain.auth.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    // RefreshToken은 refresh_id를 식별자로, user_id를 사용자 외래 키로 사용한다.
    void refreshToken_usesRefreshIdAndUserForeignKey() throws NoSuchFieldException {
        Table table = RefreshToken.class.getAnnotation(Table.class);
        Id id = RefreshToken.class.getDeclaredField("refreshId").getAnnotation(Id.class);
        GeneratedValue generatedValue = RefreshToken.class.getDeclaredField("refreshId").getAnnotation(GeneratedValue.class);
        Column refreshIdColumn = RefreshToken.class.getDeclaredField("refreshId").getAnnotation(Column.class);
        JoinColumn userColumn = RefreshToken.class.getDeclaredField("user").getAnnotation(JoinColumn.class);

        assertThat(table.name()).isEqualTo("refresh_token");
        assertThat(id).isNotNull();
        assertThat(generatedValue).isNotNull();
        assertThat(refreshIdColumn.name()).isEqualTo("refresh_id");
        assertThat(userColumn.name()).isEqualTo("user_id");
        assertThat(userColumn.referencedColumnName()).isEqualTo("users_id");
        assertThat(RefreshToken.class.getDeclaredField("token").getAnnotation(Column.class).unique()).isTrue();
    }
}
