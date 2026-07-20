package com.example.studymate.member.service;

import com.example.studymate.member.dto.MemberRequestDto;
import com.example.studymate.member.dto.MemberResponseDto;
import com.example.studymate.member.entity.Member;
import com.example.studymate.member.entity.MemberRole;
import com.example.studymate.member.entity.RoleName;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.member.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    
    // SecurityConfig에 빈으로 등록해둔 BCrypt 인코더를 주입받아 사용
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // 회원 저장 + 권한 매핑 저장이 하나의 트랜잭션으로 묶임
    public MemberResponseDto registerMember(MemberRequestDto memberDto) {
        MemberResponseDto responseDto = new MemberResponseDto();

        try {
            // 1. DTO 데이터를 기반으로 Member 엔티티 생성 (비밀번호는 반드시 암호화)
            Member member = Member.builder()
                    .name(memberDto.getName())
                    .email(memberDto.getEmail())
                    .password(passwordEncoder.encode(memberDto.getPassword()))
                    .nickname(memberDto.getNickname())
                    .build();
            
            // 2. 회원가입 시 기본 권한인 ROLE_USER 부여
            roleRepository.findByRoleName(RoleName.ROLE_USER).ifPresent(role -> {
                MemberRole memberRole = new MemberRole(member, role);
                member.getMemberRoles().add(memberRole);
            });

            // 3. DB에 회원 정보 및 권한 정보 영속화 (저장)
            Member savedMember = memberRepository.save(member);
            log.debug("Member Registered: {}", savedMember.getEmail());

            // 4. 컨트롤러로 전달할 성공 결과 세팅
            responseDto.setResult("success");
        } catch (Exception e) {
            log.error("Member Registration Failed", e);
            
            // 에러 발생 시 진행되던 DB 쿼리들을 모두 롤백처리 (회원 데이터 꼬임 방지)
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            responseDto.setResult("fail");
        }

        return responseDto;
    }
}

