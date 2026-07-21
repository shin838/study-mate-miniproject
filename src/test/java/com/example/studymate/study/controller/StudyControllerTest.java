package com.example.studymate.study.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.study.service.StudyService;

@WebMvcTest(StudyController.class)
@AutoConfigureMockMvc(addFilters = false) // security 병합 전이므로 필터 끔
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudyService studyService;

    @Test
    void test1() throws Exception { // 정상적인 스터디 생성 요청이면 201 반환
        StudyResponseDto response = new StudyResponseDto(
                10,
                "Git 스터디",
                "Git 공부하실 분",
                3,
                StudyStatus.RECRUITING,
                1,
                "테스트"
        );

        when(studyService.createStudy(any(), eq(1))) // eq(1) : controller 하드코딩 때문에 임시로 사용
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
                .andExpect(jsonPath("$.leaderId").value(1))
                .andExpect(jsonPath("$.leaderNickname").value("테스트"));
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