-- application-local.properties.example 파일 복사해서 
-- application-local.properties 파일 생성 후 로컬 설정 해서 실행

-- 1. role 테이블 (시스템 글로벌 권한)
CREATE TABLE role (
role_id INT AUTO_INCREMENT PRIMARY KEY,
role_name VARCHAR(30) NOT NULL UNIQUE
);
-- 2. member 테이블
CREATE TABLE member (
member_id INT AUTO_INCREMENT PRIMARY KEY,
email VARCHAR(100) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
name VARCHAR(50) NOT NULL,
nickname VARCHAR(50) NOT NULL
);
-- 3. member_role 테이블 (회원 - 권한 매핑)
CREATE TABLE member_role (
id INT AUTO_INCREMENT PRIMARY KEY,
member_id INT NOT NULL,
role_id INT NOT NULL,
FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
);
-- 4. refresh_token 테이블
CREATE TABLE refresh_token (
member_id INT PRIMARY KEY,
token VARCHAR(255) NOT NULL,
expiry_date DATETIME NOT NULL,
FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);
-- 5. study 테이블 (leader_id 제거, creator_id와 생성시간 유지)
CREATE TABLE study (
study_id INT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(100) NOT NULL,
content TEXT NOT NULL,
max_member INT NOT NULL,
status VARCHAR(30) NOT NULL,
creator_id INT NOT NULL, -- 스터디 작성자
created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (creator_id) REFERENCES member(member_id)
);
-- 6. study_application 테이블
CREATE TABLE study_application (
application_id INT AUTO_INCREMENT PRIMARY KEY,
member_id INT NOT NULL,
study_id INT NOT NULL,
application_status VARCHAR(30) NOT NULL,
UNIQUE KEY uk_member_study (member_id, study_id),
FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
FOREIGN KEY (study_id) REFERENCES study(study_id) ON DELETE CASCADE
);
-- 7. study_member 테이블 (study_role 다시 추가)
CREATE TABLE study_member (
study_member_id INT AUTO_INCREMENT PRIMARY KEY,
study_id INT NOT NULL,
member_id INT NOT NULL,
study_role VARCHAR(30) NOT NULL DEFAULT 'MEMBER', -- 'LEADER' 또는 'MEMBER'로 구분
joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
UNIQUE KEY uk_study_member (study_id, member_id),
FOREIGN KEY (study_id) REFERENCES study(study_id) ON DELETE CASCADE,
FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- 회원가입 테스트 전에 role 추기
INSERT INTO role (role_id, role_name) VALUES (1, 'ROLE_USER');
INSERT INTO role (role_id, role_name) VALUES (2, 'ROLE_ADMIN');