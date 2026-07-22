package com.example.studymate.study.service;

import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;

public interface StudyService {
	
	StudyResponseDto createStudy(StudyRequestDto request, Integer memberId);
}
