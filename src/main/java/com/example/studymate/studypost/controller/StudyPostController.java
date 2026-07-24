package com.example.studymate.studypost.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.studypost.dto.StudyPostRequestDto;
import com.example.studymate.studypost.dto.StudyPostResponseDto;
import com.example.studymate.studypost.service.StudyPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StudyPostController {

	private final StudyPostService studyPostService;

	@PostMapping("/studies/{studyId}/posts")
	public ResponseEntity<StudyPostResponseDto> createPost(
			@PathVariable("studyId") Integer studyId,
			@Valid @RequestBody StudyPostRequestDto requestDto,
			@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		
		StudyPostResponseDto responseDto = studyPostService.createPost(studyId, userDetails.getId(), requestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@GetMapping("/studies/{studyId}/posts")
	public ResponseEntity<Page<StudyPostResponseDto>> getPosts(
			@PathVariable("studyId") Integer studyId,
			@PageableDefault(size = 20) Pageable pageable, 
			@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		
		Page<StudyPostResponseDto> response = studyPostService.getPosts(studyId, userDetails.getId(), pageable);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/studies/{studyId}/posts/{postId}")
	public ResponseEntity<Void> deletePost(
			@PathVariable("studyId") Integer studyId,
			@PathVariable("postId") Integer postId, 
			@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		
		studyPostService.deletePost(studyId, postId, userDetails.getId());

		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/studies/{studyId}/posts/{postId}")
	public ResponseEntity<StudyPostResponseDto> updatePost(
	        @PathVariable("studyId") Integer studyId,
	        @PathVariable("postId") Integer postId,
	        @Valid @RequestBody StudyPostRequestDto requestDto,
	        @AuthenticationPrincipal CustomUserDetails userDetails
	) {
	    StudyPostResponseDto responseDto =
	            studyPostService.updatePost(
	                    studyId,
	                    postId,
	                    userDetails.getId(),
	                    requestDto
	            );

	    return ResponseEntity.ok(responseDto);
	}
}