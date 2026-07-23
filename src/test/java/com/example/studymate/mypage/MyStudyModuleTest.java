package com.example.studymate.mypage;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.mypage.controller.MyPageController;
import com.example.studymate.mypage.repository.MyStudyRepository;
import com.example.studymate.mypage.service.MyStudyService;
import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.repository.StudyRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MyStudyModuleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MyPageController myPageController;

    @Autowired
    private MyStudyService myStudyService;

    @Autowired
    private MyStudyRepository myStudyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Test
    @Order(1)
    void 내가만든스터디_모듈_DI_확인() {
        assertNotNull(mockMvc);
        assertNotNull(myPageController);
        assertNotNull(myStudyService);
        assertNotNull(myStudyRepository);
    }

    @Test
    @Order(2)
    void 로그인한_회원이_만든_스터디만_조회된다() throws Exception {
        Member loginMember = saveMember("my-study-owner");
        Member otherMember = saveMember("my-study-other");

        studyRepository.saveAndFlush(
                Study.create("내가 만든 스터디", "내 스터디", 4, loginMember));
        studyRepository.saveAndFlush(
                Study.create("다른 회원 스터디", "다른 스터디", 4, otherMember));

        mockMvc.perform(get("/my/studies")
                        .with(authentication(login(loginMember))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("내가 만든 스터디")))
                .andExpect(content().string(not(containsString("다른 회원 스터디"))));
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
