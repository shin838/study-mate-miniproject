package com.example.studymate.study.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class StudyRequestDtoTest {

	private Validator validator;
	
	@BeforeEach
    void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void test1() { // 제목이 비어있으면 검증에 실패하는지

        StudyRequestDto request = new StudyRequestDto(
                "",
                "Git 공부하실 분",
                3
        );

        Set<ConstraintViolation<StudyRequestDto>> violations = 
        		validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(
                "제목을 입력하세요.",
                violations.iterator().next().getMessage()
        );
    }
    
    @Test
    void test2() { // 내용이 비어있으면 검증에 실패하는지

        StudyRequestDto request = new StudyRequestDto(
                "Git 스터디",
                "",
                3
        );

        Set<ConstraintViolation<StudyRequestDto>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(
                "내용을 입력하세요.",
                violations.iterator().next().getMessage()
        );
    }
    
    @Test
    void test3() { // 제목이 100자를 초과하면 검증에 실패하는지
        
    	String longTitle = "t".repeat(101);

        StudyRequestDto request = new StudyRequestDto(
                longTitle,
                "Git 공부하실 분",
                3
        );

        Set<ConstraintViolation<StudyRequestDto>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        ConstraintViolation<StudyRequestDto> violation =
                violations.iterator().next();

        assertEquals("title", violation.getPropertyPath().toString());
    }
    
    @Test
    void test4() { // 최대 인원이 null 이면 검증에 실패하는지

    	StudyRequestDto request = new StudyRequestDto(
                "Git 스터디",
                "Git 공부하실 분",
                null
        );

        Set<ConstraintViolation<StudyRequestDto>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        ConstraintViolation<StudyRequestDto> violation =
                violations.iterator().next();

        assertEquals(
                "maxMember",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "최대 인원을 입력하세요.",
                violation.getMessage()
        );
    }
    
    @Test
    void test5() { // 최대 인원이 0이면 검증에 실패하는지

    	StudyRequestDto request = new StudyRequestDto(
                "Git 스터디",
                "Git 공부하실 분",
                0
        );

        Set<ConstraintViolation<StudyRequestDto>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        ConstraintViolation<StudyRequestDto> violation =
                violations.iterator().next();

        assertEquals(
                "maxMember",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "최대 인원은 1명 이상이어야 합니다.",
                violation.getMessage()
        );
    }
    
    @Test
    void test6() { // 정상적인 스터디 생성 요청
        StudyRequestDto request = new StudyRequestDto(
                "Git 스터디",
                "Git 공부하실 분",
                3
        );

        Set<ConstraintViolation<StudyRequestDto>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
