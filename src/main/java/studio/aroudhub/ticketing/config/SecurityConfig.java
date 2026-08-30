package studio.aroudhub.ticketing.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import studio.aroudhub.ticketing.domain.auth.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // JWT 인증 필터를 주입한다.
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // 비밀번호 저장용 BCrypt 인코더를 제공한다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // React 클라이언트의 쿠키 포함 API 요청을 허용하는 CORS 정책을 구성한다.
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${auth.cors.allowed-origin}") String allowedOrigin) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /*
    * security filter 구성하는 메서드
    * 1. JwAutenticationFilter 실행
    * 2. JWT에서 email, role 추출하여 Authentication 생성 및 SecurityContextHolder에 저장
    * 3. AuthorizaionFilter 실행
    * 4. 필터 구성 후에 securityFilterChain 메서드 코드 동작
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // 등록된 CorsConfigurationSource 를 적용시킴.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // stateless 방식
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(
                        (request, response, authenticationException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/events",
                                "/api/events/**",
                                "/api/concerts",
                                "/api/concerts/**",
                                "/api/auth/login",
                                "/api/auth/signup",
                                "/api/auth/refresh"
                        ).permitAll() // 위 URL은 jwt 토큰없이 접근 가능.
                        .requestMatchers("/api/admin", "/api/admin/**").hasRole("ADMIN") // 해당 엔드포인트는 ADMIN role만 접근허용
                        .anyRequest().authenticated() // permitAll()에서 설정하지 않은 나머지 주소는 무조건 로그인(인증) 필요.
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .build();
    }
}
