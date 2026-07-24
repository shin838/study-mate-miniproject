package com.example.studymate.mypage.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyParticipatingStudyDetailResponseDto {

	private Integer studyId;
	private String title;
	private String content;
	private int maxMember;

	// 현재 참여 중인 일반 MEMBER 수
	private int currentMemberCount;

	private StudyStatus status;
	private Integer creatorId;
	private String creatorNickname;
	private LocalDateTime createdAt;
	private StudyRole myRole;

	// LEADER와 MEMBER를 모두 포함한 전체 스터디원 목록
	private List<StudyMemberResponseDto> members;

	public static MyParticipatingStudyDetailResponseDto from(Study study, StudyRole myRole,
			List<StudyMember> studyMembers) {

		List<StudyMemberResponseDto> memberResponses = studyMembers.stream().map(StudyMemberResponseDto::from).toList();

		int currentMemberCount = (int) studyMembers.stream()
				.filter(studyMember -> studyMember.getStudyRole() == StudyRole.MEMBER).count();

		return new MyParticipatingStudyDetailResponseDto(
				study.getStudyId(), 
				study.getTitle(), 
				study.getContent(),
				study.getMaxMember(), 
				currentMemberCount, 
				study.getStatus(), 
				study.getCreator().getMemberId(),
				study.getCreator().getNickname(), 
				study.getCreatedAt(), 
				myRole, 
				memberResponses
		);
	}
}