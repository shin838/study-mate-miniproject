package com.example.studymate.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyApplicationResponseDto {

    private Integer applicationId;

    private Integer memberId;
    private String memberName;

    private Integer studyId;
    private String studyTitle;

    private String applicationStatus;
}
