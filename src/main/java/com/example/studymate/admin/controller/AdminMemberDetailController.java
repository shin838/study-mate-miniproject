package com.example.studymate.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.admin.dto.AdminMemberDetailResponseDto;
import com.example.studymate.admin.service.AdminMemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberDetailController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/{memberId}")
    public AdminMemberDetailResponseDto getMemberDetail(
            @PathVariable("memberId") Integer memberId) {
        return adminMemberService.getMemberDetail(memberId);
    }
}
