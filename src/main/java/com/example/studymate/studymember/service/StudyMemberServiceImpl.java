package com.example.studymate.studymember.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.studymember.dto.StudyLeaderTransferResponseDto;
import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;
import com.example.studymate.studymember.repository.StudyMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyMemberServiceImpl implements StudyMemberService {

	private final StudyMemberRepository studyMemberRepository;

	@Override
	public StudyLeaderTransferResponseDto transferLeader(
			Integer studyId, 
			Integer currentMemberId,
			Integer newLeaderId
	) {

		// 요청한 로그인 사용자가 해당 스터디에 참여 중인지 확인
		StudyMember currentLeader = studyMemberRepository
				.findByStudy_StudyIdAndMember_MemberId(studyId, currentMemberId)
				.orElseThrow(() -> new AccessDeniedException("해당 스터디에 참여 중인 회원이 아닙니다."));

		// 현재 리더만 리더를 양도할 수 있음
		if (currentLeader.getStudyRole() != StudyRole.LEADER) {
			throw new AccessDeniedException("현재 스터디장만 리더를 양도할 수 있습니다.");
		}

		// 자신에게 다시 양도하는 요청 차단
		if (currentMemberId.equals(newLeaderId)) {
			throw new IllegalArgumentException("자기 자신에게 리더를 양도할 수 없습니다.");
		}

		// 새로운 리더가 같은 스터디에 참여 중인지 확인
		StudyMember newLeader = studyMemberRepository.findByStudy_StudyIdAndMember_MemberId(studyId, newLeaderId)
				.orElseThrow(() -> new IllegalArgumentException("리더는 해당 스터디에 참여 중이어야 합니다."));

		// 일반 스터디원에게만 양도 가능
		if (newLeader.getStudyRole() != StudyRole.MEMBER) {
			throw new IllegalStateException("일반 스터디원에게만 리더를 양도할 수 있습니다.");
		}

		// 기존 리더와 새로운 리더의 역할 변경
		currentLeader.changeToMember();
		newLeader.changeToLeader();

		return StudyLeaderTransferResponseDto.of(currentLeader, newLeader);

	}
}