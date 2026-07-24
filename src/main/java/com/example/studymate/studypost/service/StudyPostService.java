package com.example.studymate.studypost.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.studymate.studypost.dto.StudyPostRequestDto;
import com.example.studymate.studypost.dto.StudyPostResponseDto;

public interface StudyPostService {

    StudyPostResponseDto createPost(
            Integer studyId,
            Integer memberId,
            StudyPostRequestDto requestDto
    );

    Page<StudyPostResponseDto> getPosts(
            Integer studyId,
            Integer memberId,
            Pageable pageable
    );

    void deletePost(
            Integer studyId,
            Integer postId,
            Integer memberId
    );
    
    StudyPostResponseDto updatePost(
            Integer studyId,
            Integer postId,
            Integer memberId,
            StudyPostRequestDto requestDto
    );
}