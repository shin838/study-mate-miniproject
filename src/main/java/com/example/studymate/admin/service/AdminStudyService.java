package com.example.studymate.admin.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.studymate.admin.dto.AdminStudyResponseDto;
import com.example.studymate.admin.repository.AdminStudyRepository;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminStudyService {

    private final AdminStudyRepository adminStudyRepository;

    @Transactional(readOnly = true)
    public List<AdminStudyResponseDto> getAllStudies() {
        return adminStudyRepository.findAll().stream()
                .map(AdminStudyResponseDto::from)
                .toList();
    }

    @Transactional
    public void deleteStudy(Integer studyId) {
        if (!adminStudyRepository.existsById(studyId)) {
            throw studyNotFound(studyId);
        }

        adminStudyRepository.deleteById(studyId);
    }

    @Transactional
    public AdminStudyResponseDto changeStatus(
            Integer studyId,
            StudyStatus status) {
        int updatedCount = adminStudyRepository.updateStatus(studyId, status);

        if (updatedCount == 0) {
            throw studyNotFound(studyId);
        }

        Study study = adminStudyRepository.findById(studyId)
                .orElseThrow(() -> studyNotFound(studyId));

        return AdminStudyResponseDto.from(study);
    }

    private ResponseStatusException studyNotFound(Integer studyId) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "스터디를 찾을 수 없습니다. studyId=" + studyId);
    }
}
