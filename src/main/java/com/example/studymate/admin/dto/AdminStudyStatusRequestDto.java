package com.example.studymate.admin.dto;

import com.example.studymate.study.entity.StudyStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminStudyStatusRequestDto {

    @NotNull(message = "변경할 스터디 상태를 입력하세요.")
    private StudyStatus status;
}
