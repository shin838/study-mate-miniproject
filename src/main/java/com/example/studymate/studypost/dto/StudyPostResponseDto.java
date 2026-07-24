package com.example.studymate.studypost.dto;

import java.time.LocalDateTime;

import com.example.studymate.studypost.entity.StudyPost;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyPostResponseDto {

    private Integer postId;
    private Integer studyId;

    private Integer memberId;
    private String nickname;

    private String content;
    private LocalDateTime createdAt;

    public static StudyPostResponseDto from(StudyPost studyPost) {
        return new StudyPostResponseDto(
                studyPost.getPostId(),
                studyPost.getStudy().getStudyId(),
                studyPost.getMember().getMemberId(),
                studyPost.getMember().getNickname(),
                studyPost.getContent(),
                studyPost.getCreatedAt()
        );
    }
}