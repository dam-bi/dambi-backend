---
적용: 항상
---

# AGENTS.md

## 목적

이 문서는 `ticketing` 프로젝트에서 코드를 수정하거나 새로 작성할 때 따라야 하는 작업 규칙을 정의한다.
구현 전에 항상 프로젝트 문서와 현재 코드 상태를 함께 확인하고, 작은 변경으로 검증 가능하게 끝내는 것을 기본 원칙으로 한다.

## 문서 우선 원칙

- 구현 전 [docs/project.md](C:/GithubTest/ticketing/docs/project.md)를 확인해 도메인, 상태, API, 응답 구조를 먼저 맞춘다.
- 코드 수정 및 변경, 코드 플랜 작성 시 [.cursor/rules/karpathy-guidelines.mdc](C:/GithubTest/ticketing/.cursor/rules/karpathy-guidelines.mdc)를 반드시 참조한다.
- 문서와 코드가 다르면 임의로 한쪽만 밀어붙이지 말고, 어느 쪽을 기준으로 맞출지 명확히 판단한 뒤 작업한다.
- 아직 확정되지 않은 요구사항은 추측해서 확장하지 않는다.

## Controller / Service / Repository 책임 분리

### Controller

- Controller는 HTTP 계층만 담당한다.
- 요청 URL, query parameter, path variable, request body를 받아 DTO로 매핑한다.
- 입력 검증(`@Valid` 등)과 인증 주체 확인까지 처리한다.
- 비즈니스 판단은 직접 하지 않고 Service 호출로 넘긴다.
- 응답 상태 코드와 response DTO 반환만 담당한다.
- Entity를 직접 API 응답으로 반환하지 않는다.
- Repository를 직접 호출하지 않는다.

### Service

- Service는 비즈니스 규칙의 중심이다.
- 여러 Repository를 조합해 하나의 유스케이스를 완성한다.
- 상태 전이, 권한 검증, 중복 검사, 만료 처리, 예외 발생 조건은 Service에서 판단한다.
- 트랜잭션 경계는 Service에 둔다.
- Controller나 HTTP 세부사항에 의존하지 않는다.
- `ResponseEntity`, 요청/응답 포맷 같은 웹 계층 개념을 Service에 넣지 않는다.
- 단순 CRUD처럼 보여도 도메인 규칙이 개입되면 반드시 Service를 통해 처리한다.

### Repository

- Repository는 영속성 계층만 담당한다.
- Entity 조회, 저장, 수정과 쿼리 정의까지만 맡는다.
- 비즈니스 규칙, 상태 전이, 권한 판단을 넣지 않는다.
- HTTP 개념이나 화면 응답 형태를 알지 못해야 한다.
- 반환 타입은 Entity 또는 조회 목적에 한정된 projection 수준으로 제한한다.
- Request DTO나 Response DTO를 Repository 경계까지 끌고 내려가지 않는다.

## 금지 규칙

- Controller에서 Repository를 직접 호출하지 않는다.
- Controller에서 상태 변경 로직이나 비즈니스 분기를 직접 구현하지 않는다.
- Repository에서 비즈니스 예외 판단을 하지 않는다.
- Service에서 Entity를 그대로 외부 API 응답으로 노출하지 않는다.

## 코드 수정 원칙

- 변경은 작고 직접적으로 한다.
- 사용자의 요청과 직접 관련 없는 리팩터링은 하지 않는다.
- 내 변경으로 인해 깨진 import, DTO, 테스트, 매핑은 같은 작업에서 함께 정리한다.
- 주변 코드에 문제가 보여도 현재 작업 범위를 벗어나면 보고만 하고 함부로 수정하지 않는다.
- 기존 코드 스타일과 패키지 구조를 우선 따른다.

## 코드 플랜 작성 원칙

- 작업이 여러 단계라면 구현 전에 짧은 플랜을 먼저 적는다.
- 플랜은 각 단계가 무엇을 바꾸는지와 어떻게 검증할지를 포함해야 한다.
- 플랜은 `karpathy-guidelines.mdc`의 "Think Before Coding", "Surgical Changes", "Goal-Driven Execution" 원칙을 따른다.
- 검증 기준 없는 플랜을 작성하지 않는다.

예시:

1. API 또는 도메인 규칙 확인 - verify: `docs/project.md`와 현재 코드 비교
2. 최소 범위 수정 - verify: 변경 파일이 요청 범위를 넘지 않는지 확인
3. 테스트 또는 실행 검증 - verify: 관련 테스트 통과 또는 응답 형태 확인

## 판단 기준

- 이 로직이 HTTP 요청과 무관하게 재사용되어야 하면 Service 책임이다.
- 이 로직이 DB를 어떤 조건으로 읽고 저장할지만 다루면 Repository 책임이다.
- 이 로직이 요청을 받고 응답을 만드는 데 필요하면 Controller 책임이다.

## 작업 전 체크

- 이 변경이 정말 필요한 파일만 건드리는가
- 비즈니스 규칙이 Controller나 Repository에 새지 않았는가
- 문서, 코드, 테스트가 서로 다른 말을 하고 있지 않은가
- 검증 가능한 종료 조건을 정했는가
