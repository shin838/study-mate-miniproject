package com.example.studymate.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studymate.application.entity.StudyApplication;
import com.example.studymate.member.entity.Member;
import com.example.studymate.study.entity.Study;

public interface StudyApplicationRepository extends JpaRepository<StudyApplication, Integer> {

    // 왜 Integer냐면 StudyApplication의 PK가 Integer이기 때문
//    boolean existsByMemberAndStudy(Member member, Study study);

    // 이 회원이 이 스터디에 이미 신청했는지 true/false로 확인하는 메서드
    Optional<StudyApplication> findByMemberAndStudy(Member member, Study study);

    // 이 회원이 이 스터디에 신청한 내역을 찾기
    // mypage에서 담당
//    @Query("select sa from StudyApplication sa join fetch sa.member join fetch sa.study where sa.member = :member")
//    List<StudyApplication> findByMember(@Param("member") Member member);

    // 이 스터디에 APPLIED 상태인 신청 수 세기
    // 정원 계산을 StudyMemberRepository 로 변경함
//    int countByStudyAndApplicationStatus(Study study, String applicationStatus);
}
