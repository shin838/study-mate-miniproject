package com.example.studymate.admin;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.admin.controller.AdminMemberDetailController;
import com.example.studymate.admin.controller.AdminStudyController;
import com.example.studymate.admin.repository.AdminMemberRepository;
import com.example.studymate.admin.repository.AdminStudyRepository;
import com.example.studymate.admin.service.AdminMemberService;
import com.example.studymate.admin.service.AdminStudyService;
import com.example.studymate.member.controller.AdminMemberController;
import com.example.studymate.member.entity.Member;
import com.example.studymate.study.entity.Study;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminMemberDetailController adminMemberDetailController;

    @Autowired
    private AdminMemberController adminMemberController;

    @Autowired
    private AdminStudyController adminStudyController;

    @Autowired
    private AdminMemberService adminMemberService;

    @Autowired
    private AdminStudyService adminStudyService;

    @Autowired
    private AdminMemberRepository adminMemberRepository;

    @Autowired
    private AdminStudyRepository adminStudyRepository;

    @Test
    @Order(1)
    void 관리자_모듈_DI_확인() {
        assertNotNull(mockMvc);
        assertNotNull(adminMemberDetailController);
        assertNotNull(adminMemberController);
        assertNotNull(adminStudyController);
        assertNotNull(adminMemberService);
        assertNotNull(adminStudyService);
        assertNotNull(adminMemberRepository);
        assertNotNull(adminStudyRepository);
    }

    @Test
    @Order(2)
    void 관리자는_회원_전체조회_상세조회_삭제를_할_수_있다() throws Exception {
        Member member = saveMember("admin-member");

        mockMvc.perform(get("/admin/members")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(member.getEmail())));

        mockMvc.perform(get("/admin/members/{memberId}", member.getMemberId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()));

        mockMvc.perform(delete("/admin/members/{memberId}", member.getMemberId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));

        assertFalse(adminMemberRepository.existsById(member.getMemberId()));
    }

    @Test
    @Order(3)
    void 관리자는_스터디_전체조회_상태변경_삭제를_할_수_있다() throws Exception {
        Member creator = saveMember("admin-study-creator");
        Study study = adminStudyRepository.saveAndFlush(
                Study.create("관리자 테스트 스터디", "관리자 모듈 테스트", 5, creator));

        mockMvc.perform(get("/admin/studies")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("관리자 테스트 스터디")));

        mockMvc.perform(patch("/admin/studies/{studyId}/status", study.getStudyId())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CLOSED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studyId").value(study.getStudyId()))
                .andExpect(jsonPath("$.status").value("CLOSED"));

        mockMvc.perform(delete("/admin/studies/{studyId}", study.getStudyId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        assertFalse(adminStudyRepository.existsById(study.getStudyId()));
    }

    @Test
    @Order(4)
    void 일반_회원은_관리자_API에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/admin/studies")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    private Member saveMember(String prefix) {
        Member member = Member.builder()
                .email(prefix + "-" + UUID.randomUUID() + "@test.com")
                .password("encoded-password")
                .name("테스트 회원")
                .nickname(prefix)
                .build();

        return adminMemberRepository.saveAndFlush(member);
    }
}
