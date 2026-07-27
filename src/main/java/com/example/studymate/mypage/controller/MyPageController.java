package com.example.studymate.mypage.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.mypage.dto.MyParticipatingStudyDetailResponseDto;
import com.example.studymate.mypage.dto.MyParticipatingStudyResponseDto;
import com.example.studymate.mypage.service.MyStudyService;
import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.study.dto.StudyResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyStudyService myStudyService;

    @GetMapping("/my/studies")
    public List<StudyResponseDto> getMyStudies(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return myStudyService.getMyStudies(userDetails.getId());
    }
    
    // 내가 참여 중인 스터디 목록
    @GetMapping("/my/applications")
    public List<MyParticipatingStudyResponseDto> getMyParticipatingStudies(
                    @AuthenticationPrincipal CustomUserDetails userDetails) 
    {
        return myStudyService.getMyParticipatingStudies(userDetails.getId());
    }
    
    // 내가 참여 중인 스터디 상세조회
    @GetMapping("/my/applications/{studyId}")
    public MyParticipatingStudyDetailResponseDto getMyParticipatingStudyDetail(
                    @PathVariable("studyId") Integer studyId,
                    @AuthenticationPrincipal CustomUserDetails userDetails) 
    {
        return myStudyService.getMyParticipatingStudyDetail(userDetails.getId(),studyId);
    }
}
