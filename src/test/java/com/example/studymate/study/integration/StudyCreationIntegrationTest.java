package com.example.studymate.study.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.study.repository.StudyRepository;
import com.example.studymate.study.service.StudyService;

@SpringBootTest
@Transactional
class StudyCreationIntegrationTest {

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void test() { // 회원이 스터디를 생성하면 db에 저장되는지 테스트

    	Member creator = Member.builder()
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("테스트닉네임")
                .build();

        Member savedCreator =
                memberRepository.saveAndFlush(creator);

        StudyRequestDto request = new StudyRequestDto(
                "테스트 스터디",
                "테스트 스터디 하실 분",
                5
        );

        StudyResponseDto response =
                studyService.createStudy(
                        request,
                        savedCreator.getMemberId()
                );

        assertNotNull(response.getStudyId());
        assertNotNull(response.getCreatedAt());
        assertEquals("테스트 스터디", response.getTitle());
        assertEquals("테스트 스터디 하실 분", response.getContent());
        assertEquals(5, response.getMaxMember());
        assertEquals(StudyStatus.RECRUITING, response.getStatus());
        assertEquals(
                savedCreator.getMemberId(),
                response.getCreatorId()
        );
        assertEquals(
                "테스트닉네임",
                response.getCreatorNickname()
        );

        assertTrue(
                studyRepository.existsById(response.getStudyId())
        );
    }}
