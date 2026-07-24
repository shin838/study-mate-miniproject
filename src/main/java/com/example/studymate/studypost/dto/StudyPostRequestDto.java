package com.example.studymate.studypost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyPostRequestDto {

	@NotBlank(message = "게시글 내용을 입력하세요.")
	@Size(max = 2000, message = "게시글 내용은 2000자 이하여야 합니다.")
	private String content;
}