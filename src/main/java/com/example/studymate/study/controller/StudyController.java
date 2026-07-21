package com.example.studymate.study.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.service.StudyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

	private final StudyService studyService;

	@PostMapping
	public ResponseEntity<StudyResponseDto> createStudy(
			@Valid @RequestBody StudyRequestDto studyRequestDto
	) {
		StudyResponseDto studyResponseDto = studyService.createStudy(studyRequestDto, 1); // 추후 auth 확인하고 수

		return ResponseEntity.status(HttpStatus.CREATED).body(studyResponseDto);
	}

}
