package com.example.studymate.studymember.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;

public interface StudyMemberRepository
extends JpaRepository<StudyMember, Integer> {

	// 로그인 사용자가 해당 스터디에 참여중인지 확인
	boolean existsByStudy_StudyIdAndMember_MemberId(Integer studyId, Integer memberId);
	
	// 특정 회원의 스터디 역할 조회 (리더 여부 확인)
	Optional<StudyMember> findByStudy_StudyIdAndMember_MemberId(Integer studyId, Integer memberId);
	
	// 해당 스터디의 전체 스터디원과 회원정보 조회
	@Query("""
		    select sm
		    from StudyMember sm
		    join fetch sm.member
		    where sm.study.studyId = :studyId
		    order by sm.studyRole asc, sm.joinedAt asc
		    """)
	List<StudyMember> findAllByStudyIdWithMember(@Param("studyId") Integer studyId);
	
	// 특정 회원이 실제 참여 중인 스터디 조회
    @Query("""
            select sm
            from StudyMember sm
            join fetch sm.study s
            join fetch s.creator
            where sm.member.memberId = :memberId
            order by sm.joinedAt desc
            """)
    List<StudyMember> findAllByMemberIdWithStudy(@Param("memberId") Integer memberId);
	
	// 일반 member 인원 계산
	int countByStudy_StudyIdAndStudyRole(Integer studyId, StudyRole studyRole);
	
	// 참여취소  실제 스터디원 관계 삭제
	void deleteByStudy_StudyIdAndMember_MemberId(Integer studyId, Integer memberId);
}