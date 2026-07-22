package com.example.studymate.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Builder;
import lombok.Getter;
import java.util.Collection;

@Builder
@Getter
public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    // Spring Security 필요 필드
    private final String username; // email 사용
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    
    // 로직 꼬임방지용으로 추가한 추가 필드
    private final Long id;
    private final String name;
    private final String email;
    private final String nickname;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
