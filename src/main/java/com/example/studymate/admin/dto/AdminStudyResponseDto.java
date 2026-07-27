package com.example.studymate.admin.dto;

import java.time.LocalDateTime;

import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminStudyResponseDto {

    private Integer studyId;
    private String title;
    private String content;
    private int maxMember;
    private StudyStatus status;
    private Integer creatorId;
    private String creatorNickname;
    private LocalDateTime createdAt;

    public static AdminStudyResponseDto from(Study study) {
        return AdminStudyResponseDto.builder()
                .studyId(study.getStudyId())
                .title(study.getTitle())
                .content(study.getContent())
                .maxMember(study.getMaxMember())
                .status(study.getStatus())
                .creatorId(study.getCreator().getMemberId())
                .creatorNickname(study.getCreator().getNickname())
                .createdAt(study.getCreatedAt())
                .build();
    }
}
