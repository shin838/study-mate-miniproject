package com.example.studymate.study.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.example.studymate.member.entity.Member;

class StudyTest {

	@Test
	void test1() { // 스터디를 생성하면 입력값과 모집중 상태가 설정
		Member member = Member.builder()
				.memberId(1)
				.nickname("테스트")
				.build();
		Study study = Study.create("Git 스터디", "Git에 대해 공부하실 분 모집", 3, member);
		
		assertEquals("Git 스터디", study.getTitle());
		assertEquals("Git에 대해 공부하실 분 모집", study.getContent());
		assertEquals(3, study.getMaxMember());
		assertEquals(StudyStatus.RECRUITING, study.getStatus());
		assertSame(member, study.getCreator());
	}
	
	@Test
	void test2() { // 최대 인원이 0이면 스터디 생성 불가
	    Member creator = Member.builder()
	            .memberId(1)
	            .nickname("테스트")
	            .build();

	    IllegalArgumentException exception = assertThrows(
	            IllegalArgumentException.class,
	            () -> Study.create(
	                    "Git 스터디",
	                    "Git 공부하실 분",
	                    0,
	                    creator
	            )
	    );

	    assertEquals(
	            "최대 인원은 1명 이상이어야 합니다.",
	            exception.getMessage()
	    );
	}
}
