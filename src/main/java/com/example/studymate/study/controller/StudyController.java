package com.example.studymate.study.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.study.dto.StudyListResponseDto;
import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.dto.StudyUpdateRequestDto;
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
			@Valid @RequestBody StudyRequestDto studyRequestDto,
			@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		StudyResponseDto studyResponseDto = studyService.createStudy(studyRequestDto, userDetails.getId());

		return ResponseEntity.status(HttpStatus.CREATED).body(studyResponseDto);
	}

	@GetMapping
	public ResponseEntity<Page<StudyListResponseDto>> getStudies(
			@RequestParam(name="keyword", required=false) String keyword,
			// 최신 글부터 조회
			@PageableDefault(size=10) Pageable pageable
	) {
		Page<StudyListResponseDto> studies = studyService.getStudies(keyword, pageable);
		
		return ResponseEntity.ok(studies);
	}
	
	@GetMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto> getStudy(
            @PathVariable("studyId") Integer studyId
    ) {
        StudyResponseDto study = studyService.getStudy(studyId);

        return ResponseEntity.ok(study);
    }
	
	@PutMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto> updateStudy(
            @PathVariable("studyId") Integer studyId,
            @Valid @RequestBody StudyUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StudyResponseDto updatedStudy = studyService.updateStudy(studyId, request, userDetails.getId());

        return ResponseEntity.ok(updatedStudy);
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(
            @PathVariable("studyId") Integer studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        studyService.deleteStudy(studyId, userDetails.getId());

        return ResponseEntity
                .noContent()
                .build();
    }
}
