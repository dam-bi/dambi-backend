package studio.aroudhub.ticketing.domain.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
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

        // 토큰이 없는 경우, 로그 남기고 다음 필터 진행
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.warn("JWT token warn: 헤더가 null이거나 잘못된 형식입니다");
            filterChain.doFilter(request, response); // 다음 필터로 진행
            return; // 더이상 처리하지 않음.
        }

        final String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim(); // "Bearer" 제거 후 공백 제거해서 token 얻기.

        try{
            // 토큰 만료 검사
            if (jwtTokenProvider.isAccessTokenExpired(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
                return;
            }

            // JWT에서 사용자 이메일 추출
            String email = jwtTokenProvider.getEmail(token);
            // UsernamePasswordAuthenticationToken: 사용자 이름, 비밀번호 기반으로 인증 요청 클래스
            // 이후 처리 단계에서 로그인된 요청으로 인식할 수 있도록 SecurityContext에 인증 정보를 저장한다.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    AuthorityUtils.NO_AUTHORITIES
            );

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch(Exception e){
            log.error("JWT 필터 처리 중 오류 발생: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
