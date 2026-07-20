package com.example.studymate.application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.application.dto.StudyApplicationResponseDto;
import com.example.studymate.application.service.StudyApplicationService;
import com.example.studymate.entity.StudyApplication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StudyApplicationController {

    private final StudyApplicationService studyApplicationService;

    @PostMapping("/studies/{studyId}/applications")
    public StudyApplicationResponseDto applyStudy(
            @PathVariable("studyId") Integer studyId,
            @RequestParam("memberId") Integer memberId) {

        StudyApplication studyApplication = studyApplicationService.applyStudy(
                memberId,
                studyId);

        return toResponseDto(studyApplication);
    }

    @DeleteMapping("/studies/{studyId}/applications")
    public StudyApplicationResponseDto cancelApplication(
            @PathVariable("studyId") Integer studyId,
            @RequestParam("memberId") Integer memberId) {

        StudyApplication studyApplication = studyApplicationService.cancelApplication(
                memberId,
                studyId);

        return toResponseDto(studyApplication);
    }

    @GetMapping("/my/applications")
    public List<StudyApplicationResponseDto> listMyApplications(@RequestParam("memberId") Integer memberId) {
        return studyApplicationService.listMyApplications(memberId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

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
