USE `study-mate-mini`;

SET NAMES utf8mb4;

-- 모든 샘플 계정의 로그인 비밀번호는 1234입니다.

-- 1. 권한
INSERT INTO `role` (`role_id`, `role_name`)
VALUES
    (1, 'ROLE_USER'),
    (2, 'ROLE_ADMIN');

-- 2. 회원
INSERT INTO `member`
    (`member_id`, `email`, `password`, `name`, `nickname`)
VALUES
    (
        1,
        'user1@test.com',
        '$2a$10$8BUkABO6WbaCU7sFcqfd6OgIGTGHoEdxQX/ss7pzIiDiYYZSHoPcS',
        '홍길동',
        '길동'
    ),
    (
        2,
        'user2@test.com',
        '$2a$10$8BUkABO6WbaCU7sFcqfd6OgIGTGHoEdxQX/ss7pzIiDiYYZSHoPcS',
        '김철수',
        '철수'
    ),
    (
        3,
        'applicant1@test.com',
        '$2a$10$8BUkABO6WbaCU7sFcqfd6OgIGTGHoEdxQX/ss7pzIiDiYYZSHoPcS',
        '이영희',
        '영희'
    ),
    (
        4,
        'applicant2@test.com',
        '$2a$10$8BUkABO6WbaCU7sFcqfd6OgIGTGHoEdxQX/ss7pzIiDiYYZSHoPcS',
        '박민수',
        '민수'
    ),
    (
        5,
        'admin@test.com',
        '$2a$10$8BUkABO6WbaCU7sFcqfd6OgIGTGHoEdxQX/ss7pzIiDiYYZSHoPcS',
        '관리자',
        'admin'
    ),
    (
        6,
        'delete@test.com',
        '$2a$10$8BUkABO6WbaCU7sFcqfd6OgIGTGHoEdxQX/ss7pzIiDiYYZSHoPcS',
        '삭제테스트',
        'delete-user'
    );

-- 3. 회원 권한
INSERT INTO `member_role` (`id`, `member_id`, `role_id`)
VALUES
    (1, 1, 1),
    (2, 2, 1),
    (3, 3, 1),
    (4, 4, 1),
    (5, 5, 1),
    (6, 5, 2),
    (7, 6, 1);

-- 4. 스터디
INSERT INTO `study`
    (
        `study_id`,
        `title`,
        `content`,
        `max_member`,
        `status`,
        `creator_id`,
        `created_at`
    )
VALUES
    (
        1,
        'Java 기초 스터디',
        'Java 기초 문법부터 객체지향까지 함께 공부합니다.',
        3,
        'RECRUITING',
        1,
        '2026-07-20 10:00:00'
    ),
    (
        2,
        'Spring Boot 프로젝트 스터디',
        'Spring Boot를 이용하여 팀 프로젝트를 진행합니다.',
        2,
        'RECRUITING',
        2,
        '2026-07-21 11:00:00'
    ),
    (
        3,
        'JPA 심화 스터디',
        'JPA 연관관계와 JPQL을 공부합니다.',
        2,
        'CLOSED',
        1,
        '2026-07-22 12:00:00'
    ),
    (
        4,
        '알고리즘 정원 마감 테스트',
        '참여 신청 정원 초과 검증을 위한 스터디입니다.',
        1,
        'RECRUITING',
        2,
        '2026-07-23 09:00:00'
    ),
    (
        5,
        'React 프론트엔드 스터디',
        'React 기본 문법과 상태 관리를 공부합니다.',
        5,
        'RECRUITING',
        1,
        '2026-07-23 10:00:00'
    ),
    (
        6,
        '관리자 상태 변경 테스트',
        '관리자의 스터디 상태 변경 및 삭제 테스트용입니다.',
        3,
        'RECRUITING',
        2,
        '2026-07-23 11:00:00'
    ),
    (
        7,
        'SQL 데이터베이스 스터디',
        'SQL 기본 문법과 데이터 모델링을 공부합니다.',
        4,
        'RECRUITING',
        1,
        '2026-07-23 12:00:00'
    );

-- 5. 스터디 참여 신청
INSERT INTO `study_application`
    (`application_id`, `member_id`, `study_id`, `application_status`)
VALUES
    (1, 3, 1, 'APPLIED'),
    (2, 4, 1, 'CANCELED'),
    (3, 1, 2, 'APPLIED'),
    (4, 3, 4, 'APPLIED'),
    (5, 2, 5, 'APPLIED'),
    (6, 4, 2, 'APPLIED');

-- 6. 스터디 실제 참여 회원
INSERT INTO `study_member`
    (`study_member_id`, `study_id`, `member_id`, `study_role`, `joined_at`)
VALUES
    (1, 1, 1, 'LEADER', '2026-07-20 10:00:00'),
    (2, 2, 2, 'LEADER', '2026-07-21 11:00:00'),
    (3, 3, 1, 'LEADER', '2026-07-22 12:00:00'),
    (4, 4, 2, 'LEADER', '2026-07-23 09:00:00'),
    (5, 5, 1, 'LEADER', '2026-07-23 10:00:00'),
    (6, 6, 2, 'LEADER', '2026-07-23 11:00:00'),
    (7, 7, 1, 'LEADER', '2026-07-23 12:00:00'),
    (8, 1, 3, 'MEMBER', '2026-07-21 09:00:00'),
    (9, 2, 1, 'MEMBER', '2026-07-22 09:00:00'),
    (10, 4, 3, 'MEMBER', '2026-07-23 10:00:00'),
    (11, 5, 2, 'MEMBER', '2026-07-23 13:00:00'),
    (12, 2, 4, 'MEMBER', '2026-07-23 14:00:00');

-- Refresh Token은 로그인 성공 시 자동 생성됩니다.
