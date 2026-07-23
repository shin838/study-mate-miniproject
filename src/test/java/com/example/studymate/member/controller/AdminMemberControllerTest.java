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

    @InjectMocks
      private AdminMemberController adminMemberController;

    @Mock
    private MemberService memberService;


    @Test
    @DisplayName("관리자: 특정 회원 삭제 테스트")
    void deleteMemberTest() {
        Integer targetMemberId = 5; 
        
         MemberResponseDto fakeResponse = new MemberResponseDto();
        fakeResponse.setResult("success"); 
        
        when(memberService.deleteMember(targetMemberId)).thenReturn(fakeResponse);


         MemberResponseDto result = adminMemberController.deleteMember(targetMemberId);


        assertEquals("success", result.getResult());
        
          verify(memberService, times(1)).deleteMember(targetMemberId);
    }
}
