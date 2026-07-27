package com.example.studymate.studymember.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.studymember.dto.StudyLeaderTransferRequestDto;
import com.example.studymate.studymember.dto.StudyLeaderTransferResponseDto;
import com.example.studymate.studymember.service.StudyMemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StudyMemberController {

	private final StudyMemberService studyMemberService;

	@PatchMapping("/studies/{studyId}/leader")
	public ResponseEntity<StudyLeaderTransferResponseDto> transferLeader(
			@PathVariable("studyId") Integer studyId,
			@Valid @RequestBody StudyLeaderTransferRequestDto requestDto,
			@AuthenticationPrincipal CustomUserDetails userDetails
	) {

		StudyLeaderTransferResponseDto responseDto = studyMemberService.transferLeader(
				studyId, 
				userDetails.getId(),
				requestDto.getNewLeaderId());

		return ResponseEntity.ok(responseDto);
	}
}