package com.example.studymate.studymember.dto;

import com.example.studymate.studymember.entity.StudyMember;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyLeaderTransferResponseDto {

    private Integer studyId;

    private Integer previousLeaderId;
    private String previousLeaderNickname;

    private Integer newLeaderId;
    private String newLeaderNickname;

    public static StudyLeaderTransferResponseDto of(StudyMember previousLeader, StudyMember newLeader) {

        return new StudyLeaderTransferResponseDto(
                previousLeader.getStudy().getStudyId(),
                previousLeader.getMember().getMemberId(),
                previousLeader.getMember().getNickname(),
                newLeader.getMember().getMemberId(),
                newLeader.getMember().getNickname()
        );
    }
}