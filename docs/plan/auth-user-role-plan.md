# Auth 사용자 역할(Admin/User) 도입 계획

## 현재 상태

- `User`는 `name`, `email`, `password`, `phone`만 보유하며 역할이 없다.
- Access Token에는 이메일만 저장되고, `JwtAuthenticationFilter`는 이메일 principal과 빈 authority를 `SecurityContext`에 설정한다.
- `SecurityConfig`는 공개 경로와 인증 필요 경로만 구분하며 관리자 전용 권한 규칙은 없다.
- 현재 관리자 전용 Controller나 API는 없다.

## 설계 결정

- 역할 원본은 DB의 `users.role`로 둔다.
- 역할 값은 `UserRole` enum의 `USER`, `ADMIN`으로 제한한다.
- Access Token은 기존 이메일 claim을 유지하고 역할 claim은 추가하지 않는다.
- JWT 필터는 토큰의 이메일로 현재 사용자를 조회하고 DB 역할을 `ROLE_USER` 또는 `ROLE_ADMIN` authority로 변환한다.
- 공개 회원가입은 요청 body의 `SignupRequest.role`로 `USER` 또는 `ADMIN` 역할을 지정해 생성한다.
- 초기 Admin 생성은 공개 회원가입의 역할 지정 또는 운영용 seed·마이그레이션으로 수행하며, 이후 승격은 별도 관리자 전용 절차로 수행한다.
- 향후 관리자 API는 `/api/admin/**` 아래에 두고 `hasRole("ADMIN")`을 적용한다.

## 구현 항목

1. `UserRole` enum과 `User.role` 필드를 추가하고 null을 허용하지 않는다.
2. DB 마이그레이션으로 `users.role` 컬럼을 추가하고 기존 사용자를 `USER`로 backfill한다.
3. Docker 초기화 SQL과 seed 데이터를 새 스키마에 맞춘다. 모든 프로필이 `ddl-auto: validate`이므로 스키마를 먼저 반영한다.
4. `AuthService.signup()`이 `SignupRequest.role`에 지정된 역할을 저장하도록 변경한다.
5. `JwtAuthenticationFilter`에서 이메일에 해당하는 사용자를 조회하고 역할 authority를 담은 `Authentication`을 구성한다. 존재하지 않는 사용자는 `401`로 처리한다.
6. `SecurityConfig`에 `/api/admin/**`의 Admin 권한 규칙을 추가한다. 기존 공개 이벤트·콘서트 조회 및 `/api/auth/login`, `/api/auth/signup`, `/api/auth/refresh`는 공개로 유지한다.
7. 프론트엔드가 역할 기반 UI를 제공해야 할 때만 로그인 및 `/api/auth/check` 응답에 `role` 필드 추가를 별도 API 계약 변경으로 수행한다.

## 호환성

- 이메일 claim만 가진 기존 Access Token은 DB 역할 조회 방식에서 계속 사용할 수 있다.
- 기존 로그인 응답의 `accessToken`과 사용자 정보 필드는 유지한다.
- 역할 변경과 사용자 삭제는 다음 보호 요청부터 즉시 반영된다.

## 테스트 및 수용 기준

- 회원가입은 `SignupRequest.role`에 지정한 `USER` 또는 `ADMIN` 역할을 생성한다.
- JWT 필터는 일반 사용자에게 `ROLE_USER`, 관리자에게 `ROLE_ADMIN`을 설정한다.
- `/api/admin/**`는 미인증 요청에 `401`, 일반 사용자에 `403`, 관리자에 성공을 반환한다.
- 기존 공개 조회 API와 기존 Auth API 계약은 유지된다.
- 역할 변경 또는 사용자 삭제는 다음 요청에서 권한에 반영된다.
- 스키마 migration, Auth 단위 테스트, 보안 웹 테스트가 통과한다.
