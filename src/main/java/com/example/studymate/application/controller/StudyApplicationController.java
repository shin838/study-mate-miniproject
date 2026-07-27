package com.example.studymate.application.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.application.dto.StudyApplicationResponseDto;
import com.example.studymate.application.service.StudyApplicationService;
import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.application.entity.StudyApplication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StudyApplicationController {

    private final StudyApplicationService studyApplicationService;

    @PostMapping("/studies/{studyId}/applications")
    public StudyApplicationResponseDto applyStudy(
            @PathVariable("studyId") Integer studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails//@AuthenticationPrincipal: Spring Security가 기억하고 있는 “로그인한 사용자”를 가져와라 CustomUserDetails: 프로젝트에서 만든 로그인 사용자 정보 클래스
    ) {
        Integer memberId = userDetails.getId();

        StudyApplication studyApplication = studyApplicationService.applyStudy(memberId, studyId);

        return toResponseDto(studyApplication);
    }

    @DeleteMapping("/studies/{studyId}/applications")
    public StudyApplicationResponseDto cancelApplication(
            @PathVariable("studyId") Integer studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer memberId = userDetails.getId();

        StudyApplication studyApplication = studyApplicationService.cancelApplication(
                memberId,
                studyId);

        return toResponseDto(studyApplication);
    }

    // MyPageController 에서 재정의함
//    @GetMapping("/my/applications")
//    public List<StudyApplicationResponseDto> listMyApplications(
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Integer memberId = userDetails.getId();
//
//        return studyApplicationService.listMyApplications(memberId)
//                .stream()
//                .map(this::toResponseDto)
//                .toList();
//    }

    private StudyApplicationResponseDto toResponseDto(StudyApplication studyApplication) {
        return StudyApplicationResponseDto.builder()
                .applicationId(studyApplication.getApplicationId())
                .memberId(studyApplication.getMember().getMemberId())
                .memberName(studyApplication.getMember().getName())
                .studyId(studyApplication.getStudy().getStudyId())
                .studyTitle(studyApplication.getStudy().getTitle())
                .applicationStatus(studyApplication.getApplicationStatus())
                .build();
    }
}
