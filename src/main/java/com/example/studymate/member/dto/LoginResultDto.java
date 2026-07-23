package com.example.studymate.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResultDto {
    private String result;
      private String token; // Access Token
      
    private String refreshToken; // Refresh Token
}
