package com.example.studymate.study.service;

import org.springframework.stereotype.Service;

import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.repository.StudyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

	private final StudyRepository studyRepository;
	
	@Override
	public StudyResponseDto createStudy(StudyRequestDto request) {
		Study study = Study.create(
				request.getTitle(),
				request.getContent(),
				request.getMaxMember()
			);
		
				
		Study savedStudy = studyRepository.save(study);
		
		return StudyResponseDto.from(savedStudy);
	}
}
