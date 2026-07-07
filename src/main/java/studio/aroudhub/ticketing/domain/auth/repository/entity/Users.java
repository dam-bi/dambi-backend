package studio.aroudhub.ticketing.domain.auth.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="users_id")
    private int usersId; // user 테이블의 id 의미함.

    @Column(name = "login_id", nullable = false)
    private String loginId; // login id 그자체를 의미함.

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="phone_number", nullable = false)
    private String phone_number;

}
