package studio.aroudhub.ticketing.domain.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import studio.aroudhub.ticketing.domain.auth.repository.AuthRepository;
import studio.aroudhub.ticketing.domain.auth.repository.entity.User;

import java.io.IOException;

@Slf4j
@Component
/*
* JwtAuthenticationFilter: 적절치 못한 요청 거름.
* HTTP 요청 ➜ WAS ➜ 필터 ➜ 서블릿( DispatcherServlet ) ➜ Controller
*
* 특정 URL 패턴을 적용해 URL마다 다르게 수행하는 것이 가능.
*
* 필터 예) 로그인하지 않은 사용자의 요청
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    // JWT 검증과 사용자 역할 조회에 필요한 의존성을 주입한다.
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthRepository authRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authRepository = authRepository;
    }

    @Override
    // Authorization 헤더의 JWT를 검증하고 유효하지 않으면 즉시 401 응답을 반환한다.
    // HTTP 요청(client 요청) 올때마다 실행.
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 모든 요청에서 Authorization 헤더를 읽고, 유효한 JWT가 있으면 인증 상태를 복원한다.
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 없는 요청은 인가 필터가 공개·보호 엔드포인트를 판단하도록 넘긴다.
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization 헤더 형식이 올바르지 않습니다.");
            return;
        }

        final String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim(); // "Bearer" 제거 후 공백 제거해서 token 얻기.
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 없습니다.");
            return;
        }

        final String email;
        try {
            // 토큰 만료 검사
            if (jwtTokenProvider.isAccessTokenExpired(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
                return;
            }
            // 검증된 JWT에서 인증 사용자 이메일을 추출한다.
            email = jwtTokenProvider.getEmail(token);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            log.warn("JWT 검증 실패: {}", exception.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }

        try {
            // UsernamePasswordAuthenticationToken 생성
            UsernamePasswordAuthenticationToken authentication = this.createAuthenticationToken(email);

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException exception) {
            SecurityContextHolder.clearContext();
            log.warn("사용자 인증 정보가 올바르지 않습니다: {}", exception.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용자 정보를 찾을 수 없어, 인증을 생성할 수 없습니다.");
            return;
        } catch (DataAccessException exception) {
            log.error("사용자 인증 정보 조회 중 DB 오류 발생", exception);
            throw exception;
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // 사용자 이메일과 DB 역할로 인증 객체를 생성한다.
    private UsernamePasswordAuthenticationToken createAuthenticationToken(String email) {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("존재하지 않는 사용자입니다."));
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                AuthorityUtils.createAuthorityList("ROLE_" + user.getRole().name())
        );
    }
}
