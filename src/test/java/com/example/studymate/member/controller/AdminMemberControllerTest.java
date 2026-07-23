package com.example.studymate.member.controller;

import com.example.studymate.member.dto.MemberResponseDto;
import com.example.studymate.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMemberControllerTest {

    // 1. 진짜 테스트할 대상 (우리가 구현한 원본 코드)
    @InjectMocks
    private AdminMemberController adminMemberController;

    // 2. 가짜 객체 (Service는 Controller 밖의 영역이므로 가짜로 흉내만 냄)
    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("관리자: 특정 회원 삭제 테스트")
    void deleteMemberTest() {
        // [Given] 준비
        Integer targetMemberId = 5; // 삭제할 가짜 회원 ID 번호
        
        MemberResponseDto fakeResponse = new MemberResponseDto();
        fakeResponse.setResult("success"); // 서비스가 성공했다고 가정할 가짜 응답
        
        // "memberService.deleteMember(5)가 호출되면, 무조건 'success'를 담은 fakeResponse를 줘라!" 라고 조작
        when(memberService.deleteMember(targetMemberId)).thenReturn(fakeResponse);


        // [When] 실행 (가장 중요)
        // 👉 원본 코드를 복사해서 가져오는 것이 아니라, 원본 코드의 "기능(메서드)"을 직접 호출합니다!
        MemberResponseDto result = adminMemberController.deleteMember(targetMemberId);


        // [Then] 검증
        // 1. 결과가 내가 예상한 "success"와 일치하는가?
        assertEquals("success", result.getResult());
        
        // 2. 서비스의 deleteMember 메서드가 정확히 1번 호출되었는가?
        verify(memberService, times(1)).deleteMember(targetMemberId);
    }
}
