package com.example.studymate.study.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.study.repository.StudyRepository;

@ExtendWith(MockitoExtension.class)
class StudyServiceImplTest {

	@Mock // 가짜 Repository 생성
	private StudyRepository studyRepository;
	
	@Mock
	private MemberRepository memberRepository;
	
	@InjectMocks // 가짜 Repository들을 StudyServiceImpl 에 넣는다
	private StudyServiceImpl studyService;
	
	@Test
	void test1() { // 존재하는 회원이 스터디를 생성할 경우 서비스 로직이 정상 동작하는지
	
		Integer memberId = 1;
		
		Member member = Member.builder()
				.memberId(memberId)
				.nickname("테스트")
				.build();
		
		StudyRequestDto studyRequestDto = new StudyRequestDto("Git 스터디", "Git 공부하실 분", 3);
		
		when(memberRepository.findById(memberId)) // Mockito는 기본적으로 Optional.empty()를 반환하므로 반환결과를 정한다
        		.thenReturn(Optional.of(member));

		when(studyRepository.save(any(Study.class)))
		        .thenAnswer(invocation -> invocation.getArgument(0));
		
		StudyResponseDto response =
		        studyService.createStudy(studyRequestDto, memberId);
		
		assertEquals("Git 스터디", response.getTitle());
		assertEquals("Git 공부하실 분", response.getContent());
		assertEquals(3, response.getMaxMember());
		assertEquals(StudyStatus.RECRUITING, response.getStatus());
		assertEquals(memberId, response.getLeaderId());
		assertEquals("테스트", response.getLeaderNickname());
		
		verify(memberRepository).findById(memberId); // 해당 메서드가 실제로 호출됐는지 확인
		verify(studyRepository).save(any(Study.class));
			
	}
	
	@Test
	void test2() { // DB에 존재하지 않는 회원이 스터디 생성할 경우 예외발생하는지

		Integer memberId = 999;

	    StudyRequestDto request = new StudyRequestDto(
	            "Git 스터디",
	            "Git 공부하실 분",
	            3
	    );

	    when(memberRepository.findById(memberId))
	            .thenReturn(Optional.empty()); // 회원 id 조회했지만 회원은 존재하지 않는 상태

	    assertThrows(
	            NoSuchElementException.class,
	            () -> studyService.createStudy(request, memberId)
	    );

	    verify(memberRepository).findById(memberId);
	    verify(studyRepository, never())
	            .save(any(Study.class));
	}

}
