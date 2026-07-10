package studio.aroudhub.ticketing.domain.auth.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import studio.aroudhub.ticketing.domain.auth.repository.entity.Users;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AuthServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    // users 테이블 전체 조회 테스트
    void testAuthService1(){
        List<Users> usersList = this.userRepository.findAll();
    }

    // email값으로 데이터 조회되는지 확인
    @Test
    void testSearchByEmail(){
        String email = ""; // 테스트용 이메일
        Optional<Users> ou = this.userRepository.findByEmail(email);
        if(ou.isPresent()){
            Users u =  ou.get();
            assertEquals("userId는?", u.getEmail());
        }
    }

    @Test
    // user 테이블 수정
    void testModifyUsers(){
        String email = ""; // 테스트용 이메일
        Optional<Users> ou = this.userRepository.findByEmail(email);
        assertTrue(ou.isPresent());
        Users u =  ou.get();

    }
}
