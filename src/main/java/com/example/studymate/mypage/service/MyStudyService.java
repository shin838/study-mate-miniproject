package com.example.studymate.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.mypage.repository.MyStudyRepository;
import com.example.studymate.study.dto.StudyResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyStudyService {

    private final MyStudyRepository myStudyRepository;

    @Transactional(readOnly = true)
    public List<StudyResponseDto> getMyStudies(Integer memberId) {
        return myStudyRepository.findAllByCreatorId(memberId).stream()
                .map(StudyResponseDto::from)
                .toList();
    }
}
