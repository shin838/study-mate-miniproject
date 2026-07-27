package com.example.studymate.study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;

public interface StudyRepository extends JpaRepository<Study, Integer> {

	@Query("""
			    SELECT s
			    FROM Study s
			    ORDER BY
			        CASE WHEN s.status = :recruitingStatus THEN 0 ELSE 1 END,
			        s.createdAt DESC
			""")
	Page<Study> findAllRecruitingFirst(
			@Param("recruitingStatus") 
			StudyStatus recruitingStatus, 
			Pageable pageable
	);

	@Query("""
			    SELECT s
			    FROM Study s
			    WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    ORDER BY
			        CASE WHEN s.status = :recruitingStatus THEN 0 ELSE 1 END,
			        s.createdAt DESC
			""")
	Page<Study> searchRecruitingFirst(
			@Param("keyword") String keyword,
			@Param("recruitingStatus") StudyStatus recruitingStatus, 
			Pageable pageable
	);
}
