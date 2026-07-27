package com.example.studymate.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기존 StudyRequestDto는 스터디 생성, 이건 수정용
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyUpdateRequestDto {

	@NotBlank(message = "제목을 입력하세요.")
	@Size(max = 100, message = "제목은 100자 이하여야 합니다.")
	private String title;
	
	@NotBlank(message = "내용을 입력하세요.")
	private String content;
	
	@NotNull(message = "최대 인원을 입력하세요.")
	@Positive(message = "최대 인원은 1명 이상이어야 합니다.")
	private Integer maxMember;

}
