package com.example.studymate.studymember.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.studymate.member.entity.Member;
import com.example.studymate.study.entity.Study;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "study_member",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_study_member",
            columnNames = {"study_id", "member_id"}
        )
    }
)
@Getter
@NoArgsConstructor
public class StudyMember {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_member_id")
    private Integer studyMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_role", nullable = false, length = 30)
    private StudyRole studyRole;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    private StudyMember(Study study, Member member, StudyRole studyRole) {
        this.study = study;
        this.member = member;
        this.studyRole = studyRole;
    }

    public static StudyMember createLeader(Study study, Member member) {
        return new StudyMember(study, member, StudyRole.LEADER);
    }

    public static StudyMember createMember(Study study, Member member) {
        return new StudyMember(study, member, StudyRole.MEMBER);
    }

}
