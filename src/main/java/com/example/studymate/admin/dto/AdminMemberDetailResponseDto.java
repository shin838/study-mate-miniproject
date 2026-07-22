package com.example.studymate.admin.dto;

import java.util.List;

import com.example.studymate.member.entity.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminMemberDetailResponseDto {

    private Integer memberId;
    private String email;
    private String name;
    private String nickname;
    private List<String> roles;

    public static AdminMemberDetailResponseDto from(Member member) {
        return AdminMemberDetailResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .roles(member.getMemberRoles().stream()
                        .map(memberRole -> memberRole.getRole()
                                .getRoleName()
                                .name())
                        .toList())
                .build();
    }
}
