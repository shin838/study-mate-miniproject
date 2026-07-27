package com.example.studymate.member.service;

import java.util.List;

import com.example.studymate.member.dto.MemberRequestDto;
import com.example.studymate.member.dto.MemberResponseDto;
import com.example.studymate.member.entity.Member;

public interface MemberService {
    MemberResponseDto registerMember(MemberRequestDto memberDto);
    
    // 관리자용: 전체 회원 목록 조회
    List<Member> getAllMembers();
    
    // 관리자용: 회원 삭제
    MemberResponseDto deleteMember(Integer memberId);
    
    // 일반 회원용: 자진 탈퇴
    MemberResponseDto withdraw(Integer memberId);
}
