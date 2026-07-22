package com.example.studymate.study.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.studymate.study.dto.StudyListResponseDto;
import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;

public interface StudyService {
	
	StudyResponseDto createStudy(StudyRequestDto request, Integer memberId);
	
	Page<StudyListResponseDto> getStudies(String keyword, Pageable pageable);
	
	StudyResponseDto getStudy(Integer studyId);
}
