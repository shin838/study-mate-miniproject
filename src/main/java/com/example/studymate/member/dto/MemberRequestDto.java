package com.example.studymate.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberRequestDto {
    private String email;
    private String password;
    private String name;
    private String nickname;
}
