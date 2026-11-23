# StudyFit

학습 성향 설문을 기반으로 나에게 맞는 스터디 팀을 추천하고, 팀 구성 이후의 일정·목표·출석까지 관리하는 스터디 매칭 서비스입니다.

> 데이터베이스 프로그래밍 수업 팀 프로젝트 (Spring MVC + JSP, JDBC/MyBatis)

---

## 프로젝트 개요

| 항목 | 내용 |
| --- | --- |
| 주제 | 데이터베이스를 이용하는 응용 프로그램(서비스) 개발 |
| 분류 | Matching / 추천 + 커뮤니티 관리(스터디그룹) |
| 팀 구성 | 4인 1팀 |
| 시스템 구조 | 웹 기반 client-server, 3-tier, MVC |
| Presentation Layer | HTML, CSS, JSP, JavaScript |
| Business Layer | Java (Controller / Manager) |
| Persistence Layer | SQL, JDBC, MyBatis |
| 개발 환경 | Eclipse, Oracle, SQL Developer, Erwin, Tomcat, GitHub |

### 설계 방향

- 학습 성향 설문 결과로 `StudyType`을 계산하고, 팀 조건과 개인 성향·활동 이력을 비교해 팀을 추천합니다.
- 팀 가입 이후에도 가능 시간 조율, 일정, 목표, 출석을 관리하고, 그 결과를 활동 점수로 만들어 추천에 다시 반영합니다.

---

## 주요 기능

| 구분 | 기능 |
| --- | --- |
| 회원 (FR-A) | 회원가입, 학습 성향 설문 및 재검사, 로그인, 마이페이지, 프로필 수정, 회원 탈퇴 |
| 팀 (FR-B) | 팀 생성, 팀 가입 신청, 가입 승인·거절 |
| 추천·매칭 (FR-C) | 맞춤형 팀 추천, 후보 매칭 결과 조회, 팀 구성 균형 분석 |
| 팀 활동 (FR-D) | 활동 가능 시간 등록, 팀 공통 가능 시간 계산, 팀 일정 관리, 팀·개인 목표 관리, 모임별 출석 기록, 출석률·활동 점수 조회 |

---

## 기술 스택

| 구분 | 내용 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.0.9, Spring MVC (`spring-boot-starter-webmvc`) |
| Library | Lombok |
| View | JSP, HTML, CSS, JavaScript |
| Data Access | JDBC 또는 MyBatis |
| Database | Oracle (SQL Developer, Erwin으로 설계) |
| WAS | Tomcat |
| IDE / 협업 | Eclipse, GitHub |
| Build | Gradle (group: `com.DBP`) |

---

## 기본 요구사항 충족 계획

| 요구사항 | StudyFit에서의 구현 |
| --- | --- |
| MVC 구조 | `Controller → Manager → DAO` 3계층 + JSP View |
| 테이블 5개 이상 | ERD 기준 13개: `user`, `study_profile`, `study_type`, `team`, `team_member`, `join_application`, `availability`, `team_schedule`, `attendance_record`, `activity_goal`, `recommendation`, `match_result`, `criterion_score` |
| 다양한 관계 | 1:1 (`user`–`study_profile`), 1:N (`study_type`–`study_profile`, `team`–`team_schedule`, `match_result`–`criterion_score`), M:N (`user`–`team`: 연결 테이블 `team_member`, `recommendation`–`team`: 연결 테이블 `match_result`), 식별/비식별 관계 |
| 스키마 객체·제약 | Table, View, Sequence, Index, PK / FK / NOT NULL / UNIQUE / CHECK |
| 데이터 관리·검색 | 회원·팀·일정·목표·출석 CRUD, 팀 조건별 검색 (SQL, JDBC, MyBatis) |
| Business Logic | 학습 유형 계산, 팀 매칭 점수 산정, 팀 균형 분석, 공통 가능 시간 계산, 출석률·활동 점수 산출 |

---

## 프로젝트 구조

요청은 `Controller → Manager → DAO` 순서로 처리됩니다.

```
studyfit/
├─ src/main/java/com/DBP/
│  ├─ controller/    # 요청 처리, View 선택 (UserController, TeamController ...)
│  ├─ manager/       # 비즈니스 로직 (UserManager, TeamManager ...)
│  ├─ dao/           # DB 접근 (UserDAO, TeamDAO ...)
│  ├─ domain/        # 도메인 클래스 (User, Team, TeamMember, Goal ...)
│  └─ dto/           # 화면·요청용 Form / DTO (JoinForm, ScheduleForm ...)
├─ src/main/resources/              # DB 설정, MyBatis mapper
└─ src/main/webapp/WEB-INF/views/   # JSP (user, profile, team, schedule, goal ...)
```

### 도메인 클래스

`User`, `StudyProfile`, `StudyType`, `Team`, `TeamMember`, `JoinApplication`, `Availability`, `TeamSchedule`, `AttendanceRecord`, `Goal`, `GoalProgress`, `ActivityScore`, `Recommendation`, `MatchResult`, `CriterionScore`, `TeamBalanceResult`, `BalanceItem`

---

## 개발 컨벤션

### 브랜치 유형

| 유형 | 용도 | 브랜치명 예시 |
| --- | --- | --- |
| `feat` | 새로운 기능 | `feat/12-teamSearch` |
| `fix` | 버그 수정 | `fix/18-joinDuplicateUserId` |
| `refactor` | 동작 변경 없는 코드 개선 | `refactor/23-teamManager` |
| `chore` | 설정·의존성·배포·데이터 관리 | `chore/3-projectSetup` |
| `docs` | 문서 작성·수정 | `docs/5-readme` |
| `test` | 테스트 추가·수정 | `test/25-userDao` |
| `style` | 들여쓰기·공백 등 코드 서식 변경 | `style/26-codeFormat` |

---

### 브랜치 네이밍 컨벤션

`prefix/(이슈번호)-개발내용`

- 개발 내용은 **camelCase**로 작성합니다.

`feat/12-studySurvey`

---

### 커밋 컨벤션

`prefix: 커밋 내용(#이슈번호)`

`feat: UserDAO 작성(#3)`

`fix: 회원가입 아이디 중복 검사 오류 수정(#18)`

---

### 이슈 / PR 제목 컨벤션

`[Type] 구현 내용`

PR 제목도 이슈 제목과 동일하게 작성합니다.

`[Feat] 학습 성향 설문 구현`

`[Fix] 회원가입 아이디 중복 검사 오류 수정`
