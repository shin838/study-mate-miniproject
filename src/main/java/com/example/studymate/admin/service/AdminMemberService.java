package com.example.studymate.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.studymate.admin.dto.AdminMemberDetailResponseDto;
import com.example.studymate.admin.repository.AdminMemberRepository;
import com.example.studymate.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final AdminMemberRepository adminMemberRepository;

    @Transactional(readOnly = true)
    public AdminMemberDetailResponseDto getMemberDetail(Integer memberId) {
        Member member = adminMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "회원을 찾을 수 없습니다. memberId=" + memberId));

        return AdminMemberDetailResponseDto.from(member);
    }
}
