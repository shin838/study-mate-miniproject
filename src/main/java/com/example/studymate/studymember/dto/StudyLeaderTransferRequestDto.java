package com.example.studymate.studymember.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyLeaderTransferRequestDto {

    @NotNull(message = "새로운 리더의 회원 ID는 필수입니다.")
    private Integer newLeaderId;
}