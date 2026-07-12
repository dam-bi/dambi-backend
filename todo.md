# Concerts TODO

## 즉시 수정

- `ConcertRepository.findConcertPage()`의 목록 조회 projection 수정
  - 현재 `c.price`는 `List<ConcertPrice>`인데 `ConcertListItem.price`는 `int`라서 구조가 맞지 않음
  - 목록 카드에 보여줄 가격 기준을 정해야 함
  - 예: 최저가 1개, 대표가 1개, 또는 가격 배열 별도 응답

- `ConcertListItem`과 목록 조회 쿼리의 응답 계약 재정의
  - 현재 엔티티 구조 변경이 목록 DTO에 반영되지 않음
  - 프런트가 실제로 필요한 목록 필드만 남기고 다시 정리 필요

- 상세 응답의 순환 참조/과다 노출 여부 점검
  - `ConcertDetailResponse`가 `List<ConcertPrice>`, `List<ConcertSchedule>` 엔티티를 그대로 노출 중
  - Jackson 직렬화 시 연관관계 필드까지 노출되지 않는지 확인 필요

- 날짜/시간 타입 정책 정리
  - `createdAt`은 `String`
  - `startDate`, `endDate`는 `LocalDateTime`
  - API 응답 형식을 일관되게 맞출 필요가 있음

## 후속 리팩터링

- `ConcertDetailResponse`를 API 전용 DTO로 분리
  - 현재는 엔티티를 직접 응답에 포함하고 있음
  - `PriceItem`, `ScheduleItem`, `ShowTimeItem` 같은 하위 DTO로 분리하는 편이 안전함

- `Concert` 엔티티 필드명 자바 관례에 맞게 정리
  - `running_time` -> `runningTime`
  - `age_rating` -> `ageRating`
  - `price` 컬렉션은 의미상 `prices`
  - `date` 컬렉션은 의미상 `schedules`

- 상세 응답 매핑 책임 분리
  - 현재 `ConcertDetailResponse.from(concert)`가 직접 엔티티를 매핑함
  - 매퍼 클래스로 분리하거나 서비스에서 조립하도록 변경 검토

- 테스트 보강
  - 목록 조회 테스트에 현재 엔티티 구조 반영
  - 상세 응답 DTO 직렬화 테스트 추가
  - repository projection 회귀 테스트 추가

## 아직 미구현 기능

- 회차 조회 API 추가
  - 문서 명세: `GET /concerts/{id}/schedules`
  - 날짜 선택 시 해당 콘서트의 회차 리스트 반환 필요

- 날짜 선택 기반 회차 동적 갱신 지원
  - `concertId`와 선택 날짜 기준의 회차 조회 기능 필요
  - 프런트 캘린더 UI와 연결될 응답 구조 정의 필요

- 콘서트 목록 필터/검색 기능
  - 문서상 확장 예정 항목
  - 장르, 날짜, 지역 등 필터링은 아직 없음

- 상세 페이지 전용 응답 스키마 확정
  - 공연 정보, 장소, 가격, 회차를 프런트 요구사항 기준으로 다시 고정할 필요가 있음

- 관리자용 콘서트 등록/관리 기능
  - 문서 후속 항목으로만 존재
  - 현재 백엔드 구현 없음
