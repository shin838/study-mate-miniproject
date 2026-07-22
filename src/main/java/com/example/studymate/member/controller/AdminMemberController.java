package com.example.studymate.member.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.member.dto.MemberResponseDto;
import com.example.studymate.member.entity.Member;
import com.example.studymate.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 관리자(ADMIN) 전용 회원 관리 컨트롤러
// SecurityConfig에서 /admin/** 경로는 ROLE_ADMIN 권한이 있어야만 접근 가능하도록 설정되어 있음
@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
@Slf4j
public class AdminMemberController {

    private final MemberService memberService;

    // 관리자용 전체 회원 목록 조회
    @GetMapping
    public List<Member> getMembers() {
        log.info("Admin: 전체 회원 목록 조회");
        return memberService.getAllMembers();
    }

    // 관리자용 특정 회원 강제 탈퇴
    @DeleteMapping("/{memberId}")
    public MemberResponseDto deleteMember(@PathVariable Integer memberId) {
        log.info("Admin: 회원 삭제 요청 id={}", memberId);
        return memberService.deleteMember(memberId);
    }
}
