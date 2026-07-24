package com.example.studymate.mypage.dto;

import java.time.LocalDateTime;

import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyParticipatingStudyResponseDto {

    private Integer studyId;
    private String title;

    private int maxMember;
    private StudyStatus status;

    private Integer creatorId;
    private String creatorNickname;

    private StudyRole studyRole;
    private LocalDateTime joinedAt;

    public static MyParticipatingStudyResponseDto from(
            StudyMember studyMember) {

        Study study = studyMember.getStudy();

        return new MyParticipatingStudyResponseDto(
                study.getStudyId(),
                study.getTitle(),
                study.getMaxMember(),
                study.getStatus(),
                study.getCreator().getMemberId(),
                study.getCreator().getNickname(),
                studyMember.getStudyRole(),
                studyMember.getJoinedAt()
        );
    }
}
