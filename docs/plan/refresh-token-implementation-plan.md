# JWT Refresh Token 구현 계획

## 목표

만료된 Access Token 대신 유효한 Refresh Token을 사용해 새 Access Token을 발급하고, 로그아웃 또는 무효화된 Refresh Token의 재사용을 차단한다.

## 현재 상태

- `JwtTokenProvider`에 Access Token 및 Refresh Token 발급 메서드와 각각의 만료 시간 설정이 추가되어 있다.
- `refresh_token` 테이블 DDL과 `RefreshToken` 엔터티 뼈대가 있다.
- `/api/auth/refresh` 경로와 `AuthService.refresh()` 메서드는 선언만 되어 있으며 실제 처리가 없다.
- 로그인은 Access Token만 반환하며 Refresh Token을 발급하거나 저장하지 않는다.
- `JwtToken.java`가 NUL 문자로 손상되어 전체 테스트가 컴파일되지 않는다.

## 구현 전 결정 사항

1. Refresh Token 전달 방식: `HttpOnly`, `Secure` Cookie를 기본안으로 한다. 프론트엔드가 JSON 응답 방식을 요구하면 DTO와 요청 계약을 별도로 확정한다.
2. 로그아웃 정책: 로그아웃 시 해당 사용자의 Refresh Token을 모두 삭제한다. 다중 기기 세션을 지원하게 되더라도 한 기기 로그아웃이 아닌 전체 로그아웃으로 동작한다.
3. 갱신 정책: Refresh Token rotation을 적용한다. 갱신 성공 시 기존 토큰을 폐기하고 새 Refresh Token으로 교체한다.

## 변경 계획

1. 빌드 가능한 상태를 복구한다.
   - 대상: `domain/auth/repository/DTO/JwtToken.java`, `JwtTokenProviderTest`
   - 손상된 DTO를 제거하거나 실제 응답 DTO로 구현한다.
   - 변경된 `JwtTokenProvider` 생성자 인자에 맞춰 테스트를 수정한다.

2. Refresh Token 영속화 모델을 완성한다.
   - 대상: `RefreshToken`, `RefreshTokenRepository`, `01-schema.sql`
   - 토큰 생성, 교체, 만료 시각 저장에 필요한 생성자 또는 도메인 메서드를 추가한다.
   - 사용자별 Refresh Token을 조회·전체 삭제할 Repository 메서드를 만든다.
   - 운영 환경에서는 원문 대신 해시 저장을 검토한다.

3. 로그인 처리에서 토큰 쌍을 발급한다.
   - 대상: `AuthService`, `AuthController`, 로그인 응답 DTO
   - 로그인 성공 시 Access Token과 Refresh Token을 한 번만 발급하고 Refresh Token을 저장한다.
   - `AuthController.login()`의 중복 서비스 호출을 제거한다.
   - Access Token은 응답 본문에, Refresh Token은 결정된 방식으로 전달한다.

4. Refresh API를 구현한다.
   - 대상: `AuthController`, `AuthService`, 요청·응답 DTO
   - Refresh Token을 읽어 서명, 만료, 토큰 용도, DB 저장 여부, 사용자 존재 여부를 검증한다.
   - 성공 시 새 Access Token을 반환하고, rotation 정책에 따라 Refresh Token을 교체한다.
   - 유효하지 않거나 만료됐거나 저장되지 않은 토큰에는 `401 Unauthorized`를 반환한다.

5. Access Token과 Refresh Token의 사용 목적을 분리한다.
   - 대상: `JwtTokenProvider`, `JwtAuthenticationFilter`, `SecurityConfig`
   - 인증 필터가 Access Token만 보호 API 인증에 사용할 수 있게 검증한다.
   - `/api/auth/refresh`는 Access Token 없이 접근 가능하도록 허용한다.

6. 로그아웃과 무효화를 연결한다.
   - 대상: `AuthService`, `AuthController`, `RefreshTokenRepository`
   - 로그아웃 시 해당 사용자의 Refresh Token을 모두 삭제한다.
   - 폐기되었거나 rotation 이전 토큰으로 갱신 요청하면 거부한다.

## 테스트 계획

프로덕션 코드를 변경하기 전에 다음 실패 테스트를 먼저 추가한다.

1. `JwtTokenProviderTest`
   - Refresh Token 발급 후 이메일을 읽을 수 있다.
   - Access Token과 Refresh Token의 용도가 구분된다.
   - 만료·위조 토큰은 거부된다.

2. `AuthServiceTest`
   - 정상 로그인 시 Refresh Token을 저장하고 Access Token을 반환한다.
   - 유효한 저장 Refresh Token으로 갱신하면 새 Access Token과 교체된 Refresh Token을 발급한다.
   - 저장되지 않은 토큰, 만료 토큰, 다른 사용자 토큰은 `401`로 거부한다.
   - 로그아웃 뒤 기존 Refresh Token으로 갱신하면 거부한다.
   - 사용자에게 저장된 Refresh Token이 여러 개라면 로그아웃 시 모두 삭제한다.

3. `AuthControllerTest`
   - `POST /api/auth/refresh`의 정상 응답 형식과 상태를 확인한다.
   - 누락·유효하지 않은 Refresh Token은 `401`을 확인한다.

4. `AuthSecurityTest`
   - `/api/auth/refresh`는 Access Token 없이 호출할 수 있다.
   - Refresh Token은 보호 API 인증에 사용할 수 없다.

## 완료 기준

- 전체 테스트가 컴파일되고 관련 테스트가 통과한다.
- 로그인, 갱신, 로그아웃의 Refresh Token 수명 주기가 자동화 테스트로 검증된다.
- Refresh Token은 보호 API의 Access Token으로 인정되지 않는다.
- 만료·위조·폐기·재사용 토큰이 모두 `401`로 거부된다.
- 전달 방식과 세션 정책이 API 문서에 반영된다.
