package com.example.studymate.member.service;

import com.example.studymate.member.dto.MemberRequestDto;
import com.example.studymate.member.dto.MemberResponseDto;

public interface MemberService {
    MemberResponseDto registerMember(MemberRequestDto memberDto);
}
