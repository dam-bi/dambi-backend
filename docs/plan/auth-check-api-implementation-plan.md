# Auth Check API 구현 계획

## 출처와 목표

- GitHub Issue: [#22 회원 정보 기능 조회 api 추가](https://github.com/dam-bi/dambi-backend/issues/22)
- 목표: 새로고침 등 클라이언트 재진입 시 `POST /api/auth/check`로 Access Token을 검증하고, 유효한 사용자의 정보를 반환한다.

## 확정 요구사항

### 요청

- Method: `POST`
- Path: `/api/auth/check`
- Header: `Authorization: Bearer {accessToken}`
- Request body는 사용하지 않는다.

### 성공 응답

- Status: `200 OK`
- Body: 기존 `UserInfo` 형식을 재사용한다.

```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "phone": "010-1111-2222"
}
```

- 비밀번호, Refresh Token, 내부 사용자 식별자는 응답에 포함하지 않는다.

### 실패 응답

- Access Token이 없거나, 만료되었거나, 위조되었거나, 형식이 잘못된 경우 `401 Unauthorized`를 반환한다.
- 토큰의 이메일에 해당하는 사용자가 더 이상 존재하지 않는 경우도 `401 Unauthorized`를 반환한다.
- Refresh Token은 이 API의 입력이나 응답에 사용하지 않는다. Access Token 재발급은 기존 `/api/auth/refresh`가 담당한다.

## 설계 결정

- Issue의 `boolean` 또는 `userInfo` 선택지 중 `userInfo`를 반환한다. 클라이언트가 토큰 유효성 확인과 사용자 화면 상태 복원을 한 요청으로 처리할 수 있고, 기존 로그인 응답의 `UserInfo` DTO를 재사용할 수 있다.
- 명시된 `POST /api/auth/check` 계약을 유지한다. 읽기 전용이라는 이유로 `GET`으로 변경하지 않는다.
- `/api/auth/check`는 인증 필수 경로로 둔다. 기존 `JwtAuthenticationFilter`가 Access Token을 검증하고 `SecurityContext`에 이메일을 설정한다.

## 구현 전 실패 테스트

프로덕션 코드를 변경하기 전에 다음 테스트를 추가해 현재 누락된 동작을 재현한다.

1. `AuthControllerTest`
   - 유효한 인증 정보로 요청하면 `200`과 `name`, `email`, `phone`을 반환한다.
   - 서비스가 사용자를 찾지 못하면 `401`을 반환한다.

2. `AuthSecurityTest`
   - Authorization 헤더 없이 호출하면 `401`을 반환한다.
   - 만료된 Access Token으로 호출하면 `401`을 반환한다.
   - 위조되거나 형식이 잘못된 Access Token으로 호출하면 `401`을 반환한다.

3. `AuthServiceTest`
   - 이메일에 해당하는 사용자가 있으면 `UserInfo`로 변환한다.
   - 사용자가 없으면 `401`을 반환한다.

## 구현 순서

1. `AuthService`에 이메일 기준 사용자 정보 조회 메서드를 추가한다.
2. `AuthController`에 `Authentication`에서 이메일을 받아 서비스 결과를 반환하는 `POST /api/auth/check`를 추가한다.
3. `SecurityConfig`에서 `/api/auth/check`가 `permitAll`에 포함되지 않았는지 확인한다.
4. `http/auth.http`에 정상 확인, 만료 토큰 거부, 토큰 누락 거부 시나리오를 추가한다.
5. 관련 단위·웹 계층 테스트와 전체 테스트를 실행한다.

## 변경 대상

- `src/main/java/studio/aroudhub/ticketing/domain/auth/controller/AuthController.java`
- `src/main/java/studio/aroudhub/ticketing/domain/auth/service/AuthService.java`
- `src/main/java/studio/aroudhub/ticketing/config/SecurityConfig.java` (인증 경로 검증만 필요할 수 있음)
- `src/test/java/studio/aroudhub/ticketing/domain/auth/controller/AuthControllerTest.java`
- `src/test/java/studio/aroudhub/ticketing/domain/auth/controller/AuthSecurityTest.java`
- `src/test/java/studio/aroudhub/ticketing/domain/auth/service/AuthServiceTest.java`
- `http/auth.http`

## 완료 기준

- 유효한 Access Token으로 `/api/auth/check` 호출 시 계약된 사용자 정보와 `200`을 반환한다.
- 누락·만료·위조 Access Token 및 삭제된 사용자는 모두 `401`을 반환한다.
- 응답에 민감한 인증 정보가 포함되지 않는다.
- 새 테스트와 기존 Auth 테스트, 전체 테스트가 통과한다.
