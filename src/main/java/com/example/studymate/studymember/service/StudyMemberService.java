package com.example.studymate.studymember.service;

import com.example.studymate.studymember.dto.StudyLeaderTransferResponseDto;

public interface StudyMemberService {

	StudyLeaderTransferResponseDto transferLeader(
            Integer studyId,
            Integer currentMemberId,
            Integer newLeaderId
    );
}
