package com.example.studymate.member.controller;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.member.dto.LoginResultDto;
import com.example.studymate.member.dto.MemberRequestDto;
import com.example.studymate.member.dto.MemberResponseDto;
import com.example.studymate.member.service.MemberService;
import com.example.studymate.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 화면 반환(View)이 아닌 데이터(JSON)를 주고받는 REST API 전용 컨트롤러
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 프론트엔드에서 회원가입 폼 데이터를 보내면 (@RequestBody) 이를 받아서 처리함
    @PostMapping("/register")
    public MemberResponseDto registerMember(@ModelAttribute MemberRequestDto memberDto) {
        return memberService.registerMember(memberDto);
    }
    
    // JWT 기반 로그인 엔드포인트
    @PostMapping("/login")
    public LoginResultDto login(@RequestParam("email") String email,
                                @RequestParam("password") String password) {
        LoginResultDto loginResultDto = new LoginResultDto();
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            String username = authentication.getName();
            List<String> roles = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority).toList();
            
            String token = jwtUtil.createToken(username, roles);
            loginResultDto.setResult("success");
            loginResultDto.setToken(token);
            log.info("Login successed for {}", email);
        } catch(Exception e) {
            loginResultDto.setResult("fail");
            log.warn("Login failed for {}: {}", email, e.getMessage(), e);
        }
        
        return loginResultDto;
    }
}

