package com.example.studymate.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.admin.dto.AdminStudyResponseDto;
import com.example.studymate.admin.dto.AdminStudyStatusRequestDto;
import com.example.studymate.admin.service.AdminStudyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/studies")
@RequiredArgsConstructor
public class AdminStudyController {

    private final AdminStudyService adminStudyService;

    @GetMapping
    public List<AdminStudyResponseDto> getAllStudies() {
        return adminStudyService.getAllStudies();
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(
            @PathVariable("studyId") Integer studyId) {
        adminStudyService.deleteStudy(studyId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{studyId}/status")
    public AdminStudyResponseDto changeStatus(
            @PathVariable("studyId") Integer studyId,
            @Valid @RequestBody AdminStudyStatusRequestDto request) {
        return adminStudyService.changeStatus(studyId, request.getStatus());
    }
}
