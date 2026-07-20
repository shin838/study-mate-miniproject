package com.example.studymate.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    
    // DB의 Primary Key (자동 증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    
    // 로그인 시 아이디 역할로 사용됨
    private String email;
    
    // Spring Security 단에서 BCrypt 등으로 암호화되어 저장됨
    private String password;
    
    private String name;
    
    private String nickname;
    
    // 계정 생성일 (JPA가 자동으로 시간 주입)
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // 계정 수정일 (JPA가 자동으로 시간 주입)
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    
    // 사용자 권한 리스트 (MemberRole 엔티티를 통해 Role 테이블과 다대다 매핑 해소)
    // 회원이 삭제되면 가지고 있던 권한 매핑 정보도 함께 삭제됨 (cascade)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MemberRole> memberRoles = new ArrayList<>();
}

