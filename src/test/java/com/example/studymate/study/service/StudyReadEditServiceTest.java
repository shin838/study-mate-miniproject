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
import org.springframework.security.access.AccessDeniedException;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.dto.StudyUpdateRequestDto;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.repository.StudyRepository;

@ExtendWith(MockitoExtension.class)
class StudyReadEditServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private StudyServiceImpl studyService;
    
    private Member createMember(
            Integer memberId,
            String nickname) {
        return Member.builder()
                .memberId(memberId)
                .nickname(nickname)
                .build();
    }

    private Study createStudy(Member creator) {
        return Study.create("Git 스터디", "Git 공부하실 분", 3, creator);
    }


    @Test
    void test1() { // 스터디 상세 조회 성공
        Integer studyId = 1;
        Member creator = createMember(1, "작성자");
        Study study = createStudy(creator);

        when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(study));

        StudyResponseDto response =
                studyService.getStudy(studyId);

        assertEquals("Git 스터디", response.getTitle());
        assertEquals("Git 공부하실 분", response.getContent());
        assertEquals(3, response.getMaxMember());
        assertEquals(creator.getMemberId(), response.getCreatorId());
        assertEquals("작성자", response.getCreatorNickname());

        verify(studyRepository).findById(studyId);
    }

    @Test
    void test2() { // 존재하지 않는 스터디를 상세 조회할 경우
        Integer studyId = 999;

        when(studyRepository.findById(studyId))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> studyService.getStudy(studyId)
        );

        verify(studyRepository).findById(studyId);
    }

    @Test
    void test3() { // 작성자의 스터디 수정
        Integer studyId = 1;
        Integer creatorId = 1;

        Member creator = createMember(creatorId, "작성자");
        Study study = createStudy(creator);

        StudyUpdateRequestDto request =
                new StudyUpdateRequestDto(
                        "수정된 제목",
                        "수정된 내용",
                        5
                );

        when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(study));

        StudyResponseDto response =
                studyService.updateStudy(
                        studyId,
                        request,
                        creatorId
                );

        assertEquals("수정된 제목", response.getTitle());
        assertEquals("수정된 내용", response.getContent());
        assertEquals(5, response.getMaxMember());

        verify(studyRepository).findById(studyId);
    }

    @Test
    void test4() { // 작성자가 아닐 때 스터디 수정할 경우
        Integer studyId = 1;
        Integer creatorId = 1;
        Integer otherMemberId = 2;

        Member creator = createMember(creatorId, "작성자");
        Study study = createStudy(creator);

        StudyUpdateRequestDto request =
                new StudyUpdateRequestDto(
                        "수정된 제목",
                        "수정된 내용",
                        5
                );

        when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(study));

        assertThrows(
                AccessDeniedException.class,
                () -> studyService.updateStudy(
                        studyId,
                        request,
                        otherMemberId
                )
        );

        assertEquals("Git 스터디", study.getTitle());
        assertEquals("Git 공부하실 분", study.getContent());
        assertEquals(3, study.getMaxMember());
    }

    @Test
    void test5() { // 작성자의 스터디 삭제
        Integer studyId = 1;
        Integer creatorId = 1;

        Member creator = createMember(creatorId, "작성자");
        Study study = createStudy(creator);

        when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(study));

        studyService.deleteStudy(studyId, creatorId);

        verify(studyRepository).findById(studyId);
        verify(studyRepository).delete(study);
    }

    @Test
    void test6() { // 작성자가 아닐 경우 스터디 삭제
        Integer studyId = 1;
        Integer creatorId = 1;
        Integer otherMemberId = 2;

        Member creator = createMember(creatorId, "작성자");
        Study study = createStudy(creator);

        when(studyRepository.findById(studyId))
                .thenReturn(Optional.of(study));

        assertThrows(
                AccessDeniedException.class,
                () -> studyService.deleteStudy(
                        studyId,
                        otherMemberId
                )
        );

        verify(studyRepository).findById(studyId);
        verify(studyRepository, never())
                .delete(any(Study.class));
    }

}
