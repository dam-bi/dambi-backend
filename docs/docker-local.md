# Docker Local Backend Guide

실행 위치: backend 프로젝트 최상위 디렉토리

## 1. Docker 사용법

### 최초 1회

별도 권한 설정 없이 아래 명령으로 실행합니다.

### Docker 실행

```powershell
docker compose -f docker\compose.yml up -d --build
```

백엔드와 PostgreSQL 컨테이너를 백그라운드에서 실행하고, 변경된 백엔드 코드를 포함해 이미지를 다시 빌드합니다.

### Docker 종료 및 DB 초기화

```powershell
docker compose -f docker\compose.yml down --volumes
```

컨테이너를 종료하고 PostgreSQL 볼륨을 함께 삭제합니다.

`--volumes`를 사용하면 Docker DB에 저장된 테스트 데이터가 모두 삭제됩니다.

### 코드 변경 후 다시 실행

이 Docker 환경은 테스트용이며 백엔드 코드와 DB 구조가 자주 변경됩니다.
코드를 변경한 경우 아래 두 명령을 순서대로 실행하는 것을 기본 절차로 사용합니다.

```powershell
docker compose -f docker\compose.yml down --volumes
docker compose -f docker\compose.yml up -d --build
```

## 2. 실행 상태 확인

컨테이너 상태를 확인합니다.

```powershell
docker compose -f docker\compose.yml ps
```

백엔드 로그를 확인합니다.

```powershell
docker compose -f docker\compose.yml logs -f backend
```

PostgreSQL 로그를 확인합니다.

```powershell
docker compose -f docker\compose.yml logs -f postgres
```

로그 확인을 끝낼 때는 `Ctrl+C`를 누릅니다. 컨테이너는 계속 실행됩니다.

백엔드 API 주소:

```text
http://localhost:8080
```

## 3. 기본 테스트 데이터

### 테스트 계정

- 이메일: `frontend@example.com`
- 비밀번호: `password123!`

### 기본 데이터

Docker DB가 새로 만들어질 때 프론트 연동 테스트를 위한 기본 데이터가 생성됩니다.

## 4. DB 초기화 방식

PostgreSQL 볼륨이 없는 상태에서 처음 실행하면 `docker/db/init/` 바로 아래의 SQL 파일이 파일명 순서대로 실행됩니다.

1. `docker/db/init/01-schema.sql`
2. `docker/db/init/02-users.sql`
3. `docker/db/init/03-venue.sql`
4. `docker/db/init/04-concert.sql`
5. `docker/db/init/05-concert_price.sql`
6. `docker/db/init/06-concert_schedule.sql`
7. `docker/db/init/07-event.sql`

이 순서는 `venue -> concert`, `concert -> concert_price`, `concert -> concert_schedule`, `concert -> event` 관계 때문에 유지해야 합니다.

Spring Boot는 `local` 프로필로 실행하며 JPA의 `ddl-auto=validate`를 사용합니다. 따라서 JPA가 테이블을 생성하거나 수정하지 않고, 초기화 SQL로 만들어진 스키마가 엔티티와 일치하는지만 검증합니다.

초기화 SQL을 변경했거나 DB를 처음 상태로 되돌려야 한다면 볼륨을 삭제한 뒤 다시 실행합니다.

```powershell
docker compose -f docker\compose.yml down --volumes
docker compose -f docker\compose.yml up -d --build
```

## 5. 다른 PC에서 처음 실행

1. Git과 Docker Desktop을 설치합니다.
2. Docker Desktop을 실행합니다.
3. 백엔드 저장소를 clone하고 프로젝트 최상위 디렉토리로 이동합니다.
4. `.env.example`을 복사해 `.env`를 만듭니다.

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

macOS/Linux:

```bash
cp .env.example .env
```

5. Docker가 정상 실행 중인지 확인합니다.

```powershell
docker info
```

6. Docker를 실행합니다.

```powershell
docker compose -f docker\compose.yml up -d --build
```

7. API 호출 주소를 `http://localhost:8080`으로 설정합니다.

## 6. 문제 해결

### Docker에 연결할 수 없는 경우

Docker Desktop이 실행 중인지 확인하고 다음 명령이 정상 동작하는지 확인합니다.

```powershell
docker info
```

### 8080 포트가 이미 사용 중인 경우

기존에 실행 중인 Spring Boot 서버나 8080 포트를 사용하는 다른 컨테이너를 종료한 뒤 다시 실행합니다.

### DB 초기화 SQL이 반영되지 않는 경우

초기화 SQL은 PostgreSQL 볼륨이 비어 있을 때 실행됩니다. 기존 볼륨을 삭제한 뒤 다시 실행합니다.

```powershell
docker compose -f docker\compose.yml down --volumes
docker compose -f docker\compose.yml up -d --build
```

### 백엔드가 정상 실행되지 않는 경우

최근 백엔드 로그를 확인합니다.

```powershell
docker compose -f docker\compose.yml logs --tail=200 backend
```

## 7. 자주 사용하는 명령

| 작업 | 명령 |
| --- | --- |
| 새로 빌드하고 실행 | `docker compose -f docker\compose.yml up -d --build` |
| 종료하고 DB 볼륨 삭제 | `docker compose -f docker\compose.yml down --volumes` |
| 실행 상태 확인 | `docker compose -f docker\compose.yml ps` |
| 백엔드 로그 확인 | `docker compose -f docker\compose.yml logs -f backend` |
| PostgreSQL 로그 확인 | `docker compose -f docker\compose.yml logs -f postgres` |

