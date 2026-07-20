package com.example.studymate.member.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/members")
public class AdminMemberController {

    @GetMapping
    public String getMembers() {
        // 관리자용 모든 회원 목록 조회 로직 (추후 구현)
        return "Member List";
    }

    @DeleteMapping("/{memberId}")
    public String deleteMember(@PathVariable Integer memberId) {
        // 관리자용 특정 회원 강제 탈퇴 로직 (추후 구현)
        return "Member " + memberId + " deleted";
    }
}
