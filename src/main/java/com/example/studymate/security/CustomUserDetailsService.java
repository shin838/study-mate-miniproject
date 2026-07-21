package com.example.studymate.security;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.entity.MemberRole;
import com.example.studymate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// DB에서 사용자 정보를 꺼내와서 Spring Security가 이해할 수 있는 형태(UserDetails)로 변환해주는 서비스
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 로그인 진행 시 Spring Security가 입력받은 아이디(email)를 가지고 이 메서드를 호출함
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1.  입력받은 이메일로 DB에서 회원 조회
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            List<MemberRole> memberRoles = member.getMemberRoles();

            // 2.해당 회원이 가진 권한 목록 추출  
            List<SimpleGrantedAuthority> authorities = memberRoles.stream()
                    .map(mr -> mr.getRole().getRoleName().name())
                    .map(SimpleGrantedAuthority::new) // Spring Security가 인식할 수 있는 권한 객체로 포장
                    .toList();

            // 3 Security 규격에 맞게 매핑된 CustomUserDetails 객체 반환 (비밀번호는 이 객체 내부에서 자동 검증됨)
            return CustomUserDetails.builder()
                    .username(member.getEmail())
                    .password(member.getPassword())
                    .authorities(authorities)
                    .id(member.getMemberId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .build();
            
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
    }
}

