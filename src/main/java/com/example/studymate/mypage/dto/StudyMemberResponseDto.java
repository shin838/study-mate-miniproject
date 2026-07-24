package com.example.studymate.mypage.dto;

import java.time.LocalDateTime;

import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyMemberResponseDto {

    private Integer memberId;
    private String name;
    private String nickname;
    private StudyRole studyRole;
    private LocalDateTime joinedAt;

    public static StudyMemberResponseDto from(StudyMember studyMember) {

        return new StudyMemberResponseDto(
                studyMember.getMember().getMemberId(),
                studyMember.getMember().getName(),
                studyMember.getMember().getNickname(),
                studyMember.getStudyRole(),
                studyMember.getJoinedAt()
        );
    }
}