package com.example.studymate.study.dto;

import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyResponseDto {

	private Integer studyId;
	private String title;
	private String content;
	private int maxMember;
	private StudyStatus status;
	private Integer leaderId;
	private String leaderNickname;
	
	public static StudyResponseDto from(Study study) {
		return new StudyResponseDto(
				study.getStudyId(),
				study.getTitle(),
				study.getContent(),
				study.getMaxMember(),
				study.getStatus(),
				study.getLeader().getMemberId(),
	            study.getLeader().getNickname()
			);
	}
}
