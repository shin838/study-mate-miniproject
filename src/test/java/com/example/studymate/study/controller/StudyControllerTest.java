package com.example.studymate.study.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.study.service.StudyService;

@WebMvcTest(StudyController.class)
@AutoConfigureMockMvc(addFilters = false) // Controller 단위 테스트이므로 Security 필터 제외
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudyService studyService;

    private CustomUserDetails userDetails;

    LocalDateTime createdAt =
            LocalDateTime.of(2026, 7, 21, 14, 30);

    private Integer loginMemberId;

    @BeforeEach
    void setUp() {
    	loginMemberId = 1;


        userDetails = CustomUserDetails.builder()
                .id(loginMemberId)
                .username("test@test.com")
                .email("test@test.com")
                .password("encoded-password")
                .name("테스트")
                .nickname("테스트")
                .authorities(List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                ))
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void test1() throws Exception { // 정상적인 스터디 생성 요청이면 201 반환
        StudyResponseDto response = new StudyResponseDto(
                10,
                "Git 스터디",
                "Git 공부하실 분",
                3,
                StudyStatus.RECRUITING,
                loginMemberId,
                "테스트",
                createdAt
        );

        when(studyService.createStudy(any(), eq(loginMemberId)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/studies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Git 스터디",
                                          "content": "Git 공부하실 분",
                                          "maxMember": 3
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studyId").value(10))
                .andExpect(jsonPath("$.title").value("Git 스터디"))
                .andExpect(jsonPath("$.content").value("Git 공부하실 분"))
                .andExpect(jsonPath("$.maxMember").value(3))
                .andExpect(jsonPath("$.status").value("RECRUITING"))
                .andExpect(jsonPath("$.creatorId").value(loginMemberId))
                .andExpect(jsonPath("$.creatorNickname").value("테스트"))
                .andExpect(jsonPath("$.createdAt").value("2026-07-21T14:30:00"));

        verify(studyService).createStudy(any(), eq(loginMemberId));
    }
    
    @Test
    void test2() throws Exception { // 제목이 비어있으면 400 반환
        mockMvc.perform(
                        post("/studies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "",
                                          "content": "Git 공부하실 분",
                                          "maxMember": 3
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(studyService, never())
                .createStudy(any(), any());
    }
    
    @Test
    void test3() throws Exception { // 최대 인원이 0이면 400 반환
        mockMvc.perform(
                        post("/studies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Git 스터디",
                                          "content": "Git 공부하실 분",
                                          "maxMember": 0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(studyService, never())
                .createStudy(
                        any(),
                        any(Integer.class)
                );
    }
}