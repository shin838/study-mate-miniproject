package com.example.studymate.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyRequestDto {

	@NotBlank(message = "제목을 입력하세요.")
	@Size(max = 100)
	private String title;
	
	@NotBlank(message = "내용을 입력하세요.")
	private String content;
	
	@NotNull(message = "최대 인원을 입력하세요.")
	private Integer maxMember;
}
