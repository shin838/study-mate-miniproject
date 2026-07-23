package com.example.studymate.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.application.controller.StudyApplicationController;
import com.example.studymate.application.repository.StudyApplicationRepository;
import com.example.studymate.application.service.StudyApplicationService;
import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.repository.StudyRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyApplicationModuleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyApplicationController studyApplicationController;

    @Autowired
    private StudyApplicationService studyApplicationService;

    @Autowired
    private StudyApplicationRepository studyApplicationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Test
    @Order(1)
    void 참여신청_모듈_DI_확인() {
        assertNotNull(mockMvc);
        assertNotNull(studyApplicationController);
        assertNotNull(studyApplicationService);
        assertNotNull(studyApplicationRepository);
        assertNotNull(memberRepository);
        assertNotNull(studyRepository);
    }

    @Test
    @Order(2)
    void 참여신청_조회_취소_전체흐름() throws Exception {
        Member creator = saveMember("application-creator");
        Member applicant = saveMember("application-applicant");
        Study study = studyRepository.saveAndFlush(
                Study.create("참여 신청 테스트", "참여 모듈 테스트", 3, creator));

        UsernamePasswordAuthenticationToken login = login(applicant);

        mockMvc.perform(post("/studies/{studyId}/applications", study.getStudyId())
                        .with(authentication(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationStatus").value("APPLIED"))
                .andExpect(jsonPath("$.memberId").value(applicant.getMemberId()))
                .andExpect(jsonPath("$.studyId").value(study.getStudyId()))
                .andExpect(jsonPath("$.studyTitle").value("참여 신청 테스트"));

        mockMvc.perform(get("/my/applications")
                        .with(authentication(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].applicationStatus").value("APPLIED"))
                .andExpect(jsonPath("$[0].studyId").value(study.getStudyId()));

        mockMvc.perform(delete("/studies/{studyId}/applications", study.getStudyId())
                        .with(authentication(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationStatus").value("CANCELED"))
                .andExpect(jsonPath("$.studyId").value(study.getStudyId()));
    }

    private Member saveMember(String prefix) {
        return memberRepository.saveAndFlush(Member.builder()
                .email(prefix + "-" + UUID.randomUUID() + "@test.com")
                .password("encoded-password")
                .name("테스트 회원")
                .nickname(prefix)
                .build());
    }

    private UsernamePasswordAuthenticationToken login(Member member) {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(member.getMemberId())
                .username(member.getEmail())
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .nickname(member.getNickname())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
    }
}
