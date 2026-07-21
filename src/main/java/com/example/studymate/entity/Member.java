package com.example.studymate.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Integer memberId;

    // 로그인 시 아이디 역할로 사용됨
    private String email;

    // Spring Security 단에서 BCrypt 등으로 암호화되어 저장됨
    private String password;

    private String name;

    private String nickname;

    // 사용자 권한 리스트 (MemberRole 엔티티를 통해 Role 테이블과 다대다 매핑 해소)
    // 회원이 삭제되면 가지고 있던 권한 매핑 정보도 함께 삭제됨 (cascade)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MemberRole> memberRoles = new ArrayList<>();
}
