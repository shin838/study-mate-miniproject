package com.example.studymate.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studymate.entity.Member;
import com.example.studymate.entity.Study;
import com.example.studymate.entity.StudyApplication;

public interface StudyApplicationRepository extends JpaRepository<StudyApplication, Integer> {
    // 왜 Integer냐면 StudyApplication에서 PK
    boolean existsByMemberAndStudy(Member member, Study study);

    // 이 회원이 이 스터디에 이미 신청했는지 true/false로 확인하는 코드
    Optional<StudyApplication> findByMemberAndStudy(Member member, Study study);

    // 이 회원이 이 스터디에 신청한 내역을 찾기
    @Query("select sa from StudyApplication sa join fetch sa.member join fetch sa.study where sa.member = :member")
    List<StudyApplication> findByMember(@Param("member") Member member);

    // 이 회원이 신청한 모든 스터디 신청 내역 찾기
    int countByStudyAndApplicationStatus(Study study, String applicationStatus);

    // 이 스터디에 APPLIED 상태인 신청 수 세기
}





