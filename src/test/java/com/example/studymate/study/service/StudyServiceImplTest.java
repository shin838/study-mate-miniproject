package com.example.studymate.study.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.dto.StudyListResponseDto;
import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.study.repository.StudyRepository;
import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;
import com.example.studymate.studymember.repository.StudyMemberRepository;

@ExtendWith(MockitoExtension.class)
class StudyServiceImplTest {

	@Mock // 가짜 Repository 생성
	private StudyRepository studyRepository;
	
	@Mock
	private MemberRepository memberRepository;
	
	@Mock
	private StudyMemberRepository studyMemberRepository;
	
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
		assertEquals(memberId, response.getCreatorId());
		assertEquals("테스트", response.getCreatorNickname());
		
		verify(memberRepository).findById(memberId); // 해당 메서드가 실제로 호출됐는지 확인
		verify(studyRepository).save(any(Study.class));
		
	     // StudyMemberRepository.save()로 전달된 객체를 가져와
	     // 개설자가 LEADER로 저장됐는지 확인한다
	    ArgumentCaptor<StudyMember> captor =ArgumentCaptor.forClass(StudyMember.class);

	    verify(studyMemberRepository).save(captor.capture());

	    StudyMember savedLeader = captor.getValue();

	    assertEquals(StudyRole.LEADER, savedLeader.getStudyRole());

	    assertEquals(memberId,savedLeader.getMember().getMemberId());

	    assertEquals(member, savedLeader.getStudy().getCreator());
			
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
	    verify(studyMemberRepository, never())
	    		.save(any(StudyMember.class));
	}

	@Test
    void test3() { // 검색어가 없는 상황 (모집중 우선 목록 조회)
		Member creator = Member.builder()
	            .memberId(1)
	            .nickname("테스트")
	            .build();

	    Study study = Study.create(
	            "Git 스터디",
	            "Git 공부하실 분",
	            3,
	            creator
	    );

	    Pageable pageable = PageRequest.of(0, 10);

	    Page<Study> studyPage = new PageImpl<>(
	            List.of(study),
	            pageable,
	            1
	    );

	    when(
	            studyRepository.findAllRecruitingFirst(
	                    StudyStatus.RECRUITING,
	                    pageable
	            )
	    ).thenReturn(studyPage);

	    Page<StudyListResponseDto> response =
	            studyService.getStudies(null, pageable);

	    assertEquals(1, response.getTotalElements());
	    assertEquals(1, response.getContent().size());
	    assertEquals(
	            "Git 스터디",
	            response.getContent().get(0).getTitle()
	    );
	    assertEquals(
	            "테스트",
	            response.getContent().get(0).getCreatorNickname()
	    );

	    verify(studyRepository).findAllRecruitingFirst(
	            StudyStatus.RECRUITING,
	            pageable
	    );

	    verify(studyRepository, never()).searchRecruitingFirst(
	            any(),
	            any(),
	            any()
	    );
    }
	
	@Test
    void test4() { // 검색어가 있는 상황
		String keyword = "Git";

	    Member creator = Member.builder()
	            .memberId(1)
	            .nickname("테스트")
	            .build();

	    Study study = Study.create(
	            "Git 스터디",
	            "Git 공부하실 분",
	            3,
	            creator
	    );

	    Pageable pageable = PageRequest.of(0, 10);

	    Page<Study> studyPage = new PageImpl<>(
	            List.of(study),
	            pageable,
	            1
	    );

	    when(
	            studyRepository.searchRecruitingFirst(
	                    keyword,
	                    StudyStatus.RECRUITING,
	                    pageable
	            )
	    ).thenReturn(studyPage);

	    Page<StudyListResponseDto> response =
	            studyService.getStudies(keyword, pageable);

	    assertEquals(1, response.getTotalElements());
	    assertEquals(
	            "Git 스터디",
	            response.getContent().get(0).getTitle()
	    );

	    verify(studyRepository).searchRecruitingFirst(
	            keyword,
	            StudyStatus.RECRUITING,
	            pageable
	    );

	    verify(studyRepository, never()).findAllRecruitingFirst(
	            any(),
	            any()
	    );
	}
}
