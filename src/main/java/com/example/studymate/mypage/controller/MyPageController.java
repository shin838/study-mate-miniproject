package com.example.studymate.mypage.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
