package com.example.studymate.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor // 테스트값 넣기 쉽도록
public class StudyRequestDto {

	@NotBlank(message = "제목을 입력하세요.")
	@Size(max = 100)
	private String title;
	
	@NotBlank(message = "내용을 입력하세요.")
	private String content;
	
	@NotNull(message = "최대 인원을 입력하세요.")
	@Positive(message = "최대 인원은 1명 이상이어야 합니다.")
	private Integer maxMember;
}
