-- UC-D-01 활동 가능 시간 (Oracle)
-- 전제: team_member(team_member_id, team_id, member_id, ...) 테이블이 이미 있다.
-- 시간은 Oracle 에 TIME 타입이 없어 'HH:mm' 문자열로 저장한다 (문자열 비교 = 시간 비교).

CREATE SEQUENCE seq_availability START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE availability (
    availability_id  NUMBER        PRIMARY KEY,
    team_member_id   NUMBER        NOT NULL,
    day_of_week      VARCHAR2(9)   NOT NULL,   -- java.time.DayOfWeek 이름 (MONDAY ...)
    start_time       VARCHAR2(5)   NOT NULL,   -- HH:mm
    end_time         VARCHAR2(5)   NOT NULL,   -- HH:mm
    CONSTRAINT fk_availability_team_member
        FOREIGN KEY (team_member_id) REFERENCES team_member (team_member_id) ON DELETE CASCADE,
    CONSTRAINT ck_availability_day
        CHECK (day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    CONSTRAINT ck_availability_start_fmt
        CHECK (REGEXP_LIKE(start_time, '^([01][0-9]|2[0-3]):[0-5][0-9]$')),
    CONSTRAINT ck_availability_end_fmt
        CHECK (REGEXP_LIKE(end_time, '^([01][0-9]|2[0-3]):[0-5][0-9]$')),
    CONSTRAINT ck_availability_order
        CHECK (start_time < end_time)
);

CREATE INDEX idx_availability_team_member ON availability (team_member_id);
