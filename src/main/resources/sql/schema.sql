-- application-local.properties.example 파일 복사해서 
-- application-local.properties 파일 생성 후 로컬 설정 해서 실행

-- 필요시 테이블 삭제부터 실행
-- =========================================================
-- 1. 데이터베이스 생성 및 선택
-- =========================================================

CREATE DATABASE IF NOT EXISTS `study-mate-mini`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `study-mate-mini`;

SET NAMES utf8mb4;


-- =========================================================
-- 2. 기존 테이블 삭제
-- 외래 키로 연결된 하위 테이블부터 삭제합니다.
-- =========================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `study_application`;
DROP TABLE IF EXISTS `study_member`;
DROP TABLE IF EXISTS `study`;
DROP TABLE IF EXISTS `refresh_token`;
DROP TABLE IF EXISTS `member_role`;
DROP TABLE IF EXISTS `member`;
DROP TABLE IF EXISTS `role`;

SET FOREIGN_KEY_CHECKS = 1;


-- =========================================================
-- 3. 시스템 권한
-- 사용 권한: ROLE_USER, ROLE_ADMIN
-- =========================================================

CREATE TABLE `role` (
    `role_id` INT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 4. 회원
-- =========================================================

CREATE TABLE `member` (
    `member_id` INT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `nickname` VARCHAR(50) NOT NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 5. 회원과 시스템 권한 연결
-- =========================================================

CREATE TABLE `member_role` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `member_id` INT NOT NULL,
    `role_id` INT NOT NULL,

    CONSTRAINT `uk_member_role`
        UNIQUE (`member_id`, `role_id`),

    CONSTRAINT `fk_member_role_member`
        FOREIGN KEY (`member_id`)
        REFERENCES `member` (`member_id`)
        ON DELETE CASCADE,

    CONSTRAINT `fk_member_role_role`
        FOREIGN KEY (`role_id`)
        REFERENCES `role` (`role_id`)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 6. Refresh Token
-- =========================================================

CREATE TABLE `refresh_token` (
    `member_id` INT PRIMARY KEY,
    `token` VARCHAR(255) NOT NULL,
    `expiry_date` DATETIME NOT NULL,

    CONSTRAINT `fk_refresh_token_member`
        FOREIGN KEY (`member_id`)
        REFERENCES `member` (`member_id`)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 7. 스터디
--
-- creator_id: 스터디를 생성한 회원
-- status:
--   RECRUITING = 모집 중
--   CLOSED     = 모집 종료
-- =========================================================

CREATE TABLE `study` (
    `study_id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `max_member` INT NOT NULL,
    `status` VARCHAR(30) NOT NULL,
    `creator_id` INT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT `chk_study_max_member`
        CHECK (`max_member` >= 1),

    CONSTRAINT `chk_study_status`
        CHECK (`status` IN ('RECRUITING', 'CLOSED')),

    CONSTRAINT `fk_study_creator`
        FOREIGN KEY (`creator_id`)
        REFERENCES `member` (`member_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 8. 스터디 실제 참여 회원
-- =========================================================

CREATE TABLE `study_member` (
    `study_member_id` INT AUTO_INCREMENT PRIMARY KEY,
    `study_id` INT NOT NULL,
    `member_id` INT NOT NULL,
    `study_role` VARCHAR(30) NOT NULL DEFAULT 'MEMBER',
    `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT `uk_study_member`
        UNIQUE (`study_id`, `member_id`),

    CONSTRAINT `chk_study_member_role`
        CHECK (`study_role` IN ('LEADER', 'MEMBER')),

    CONSTRAINT `fk_study_member_study`
        FOREIGN KEY (`study_id`)
        REFERENCES `study` (`study_id`)
        ON DELETE CASCADE,

    CONSTRAINT `fk_study_member_member`
        FOREIGN KEY (`member_id`)
        REFERENCES `member` (`member_id`)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;


-- =========================================================
-- 9. 스터디 참여 신청
-- =========================================================

CREATE TABLE `study_application` (
    `application_id` INT AUTO_INCREMENT PRIMARY KEY,
    `member_id` INT NOT NULL,
    `study_id` INT NOT NULL,
    `application_status` VARCHAR(30) NOT NULL,

    CONSTRAINT `uk_application_member_study`
        UNIQUE (`member_id`, `study_id`),

    CONSTRAINT `chk_application_status`
        CHECK (`application_status` IN ('APPLIED', 'CANCELED')),

    CONSTRAINT `fk_application_member`
        FOREIGN KEY (`member_id`)
        REFERENCES `member` (`member_id`)
        ON DELETE CASCADE,

    CONSTRAINT `fk_application_study`
        FOREIGN KEY (`study_id`)
        REFERENCES `study` (`study_id`)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;