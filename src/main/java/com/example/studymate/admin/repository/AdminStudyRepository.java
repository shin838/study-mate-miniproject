package com.example.studymate.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;

public interface AdminStudyRepository extends JpaRepository<Study, Integer> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Study s set s.status = :status where s.studyId = :studyId")
    int updateStatus(
            @Param("studyId") Integer studyId,
            @Param("status") StudyStatus status);
}
