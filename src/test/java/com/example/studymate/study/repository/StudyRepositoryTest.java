package com.example.studymate.study.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void test() { // 스터디를 저장하면 id 와 게시시간이 생성되는지
        Member creator = Member.builder()
                .email("test@example.com")
                .password("test-password")
                .name("테스트")
                .nickname("테스트회원")
                .build();

        Member savedCreator =
                memberRepository.saveAndFlush(creator);

        Study study = Study.create(
                "Git 스터디",
                "Git 공부하실 분",
                3,
                savedCreator
        );

        Study savedStudy =
                studyRepository.saveAndFlush(study);

        assertNotNull(savedStudy.getStudyId());
        assertNotNull(savedStudy.getCreatedAt());
        assertEquals("Git 스터디", savedStudy.getTitle());
        assertEquals(StudyStatus.RECRUITING, savedStudy.getStatus());
        assertEquals(
                savedCreator.getMemberId(),
                savedStudy.getCreator().getMemberId()
        );
    }
}