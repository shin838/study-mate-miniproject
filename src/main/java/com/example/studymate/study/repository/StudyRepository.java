package com.example.studymate.study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studymate.study.entity.Study;

public interface StudyRepository extends JpaRepository<Study, Integer> {

	// title 칼럼에서 대소문자 구분 없이 키워드 검색 (제목 기준 검색)
	Page<Study> findByTitleContainingIgnoreCase (String keyword, Pageable pageable); 
}
