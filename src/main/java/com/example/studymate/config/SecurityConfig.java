package com.example.studymate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.studymate.security.jwt.JwtAuthenticationFilter;
import com.example.studymate.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

// 전체 애플리케이션의 인증/인가를 관리하는 최상위 보안 설정 클래스
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    // 비밀번호 암호화 방식을 BCrypt로 설정 (스프링 컨테이너에 빈으로 등록하여 어디서든 주입 가능)
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 로그인을 위한 AuthenticationManager 빈 등록
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // HTTP 요청에 대한 보안 라우팅 규칙 정의
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        // 아래 명시된 경로는 로그인 없이도(비회원도) 접근 가능하도록 허용
                        .requestMatchers(
                                "/pages/register", 
                                "/pages/login", 
                                "/members/register", 
                                "/members/login"
                        ).permitAll()
                        // 관리자 전용 경로는 ADMIN 권한이 있어야만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 그 외의 모든 요청은 로그인을 해야 접근 가능
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

