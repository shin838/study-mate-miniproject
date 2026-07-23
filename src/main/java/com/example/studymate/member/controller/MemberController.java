package com.example.studymate.member.controller;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.studymate.member.dto.LoginResultDto;
import com.example.studymate.member.dto.MemberRequestDto;
import com.example.studymate.member.dto.MemberResponseDto;
import com.example.studymate.member.service.MemberService;
import com.example.studymate.security.jwt.JwtUtil;
import com.example.studymate.security.CustomUserDetails;
import com.example.studymate.member.entity.RefreshToken;
import com.example.studymate.member.repository.RefreshTokenRepository;
import com.example.studymate.member.dto.TokenRefreshRequestDto;
import com.example.studymate.security.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

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
            String refreshTokenStr = jwtUtil.createRefreshToken(username);
            
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer memberId = customUserDetails.getId();
            
            RefreshToken refreshToken = RefreshToken.builder()
                    .memberId(memberId)
                    .token(refreshTokenStr)
                    .expiryDate(LocalDateTime.now().plusDays(7)) // 7일 후 만료
                    .build();
            refreshTokenRepository.save(refreshToken);
            
            loginResultDto.setResult("success");
            loginResultDto.setToken(token);
            loginResultDto.setRefreshToken(refreshTokenStr);
            log.info("Login successed for {}", email);
        } catch(Exception e) {
            loginResultDto.setResult("fail");
            log.warn("Login failed for {}: {}", email, e.getMessage(), e);
        }
        
        return loginResultDto;
    }
    
    // 일반 회원의 자진 탈퇴
    @DeleteMapping("/withdraw")
    public MemberResponseDto withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            MemberResponseDto response = new MemberResponseDto();
            response.setResult("fail - unauthorized");
            return response;
        }
        log.info("Member withdrawal requested by user: id={}", userDetails.getId());
        return memberService.withdraw(userDetails.getId());
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public LoginResultDto refresh(@RequestBody TokenRefreshRequestDto requestDto) {
        LoginResultDto resultDto = new LoginResultDto();
        String refreshToken = requestDto.getRefreshToken();
        
        try {
            if (jwtUtil.validateToken(refreshToken) == null) {
                resultDto.setResult("fail - invalid or expired refresh token");
                 return resultDto;
            }
            
            String username = jwtUtil.getUsernameFromToken(refreshToken);
             UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            
            
            RefreshToken storedToken = refreshTokenRepository.findById(customUserDetails.getId()).orElse(null);
            
            if (storedToken == null || !storedToken.getToken().equals(refreshToken)) {
                 resultDto.setResult("fail - refresh token not found or mismatched");
                return resultDto;
            }
            
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();
            
            
              String newAccessToken = jwtUtil.createToken(username, roles);
            
            resultDto.setResult("success");
             resultDto.setToken(newAccessToken);
            resultDto.setRefreshToken(refreshToken); // 기존 리프레시 토큰 그대로 반환 (또는 새로 갱신 가능)
            
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            resultDto.setResult("fail");
        }
        
        return resultDto;
    }
}



