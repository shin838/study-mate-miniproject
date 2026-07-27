package com.example.studymate.study.dto;

import java.time.LocalDateTime;

import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyListResponseDto {

	private Integer studyId;
    private String title;
    private int maxMember;
    private StudyStatus status;
    private Integer creatorId;
    private String creatorNickname;
    private LocalDateTime createdAt;
    
    public static StudyListResponseDto from(Study study) {
        return new StudyListResponseDto(
                study.getStudyId(),
                study.getTitle(),
                study.getMaxMember(),
                study.getStatus(),
                study.getCreator().getMemberId(),
                study.getCreator().getNickname(),
                study.getCreatedAt()
        );
    }
}
