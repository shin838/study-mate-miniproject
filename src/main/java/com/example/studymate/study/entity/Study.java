package com.example.studymate.study.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.studymate.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study")
@Getter
@NoArgsConstructor
public class Study {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_id")
	private Integer studyId;

	@Column(nullable = false, length = 100)
	private String title;

	@Lob
	@Column(nullable = false)
	private String content;

	@Column(name = "max_member", nullable = false)
	private int maxMember;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private StudyStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id", nullable = false)
	private Member creator;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private Study(String title, String content, int maxMember, Member creator) {
		this.title = title;
		this.content = content;
		this.maxMember = maxMember;
		this.creator = creator;
		this.status = StudyStatus.RECRUITING;
	}

	public static Study create(String title, String content, int maxMember, Member creator) {
		maxMemberCheck(maxMember);
		
		return new Study(title, content, maxMember, creator);
	}
	
	public void update(String title, String content, int maxMember) {
		maxMemberCheck(maxMember);
		
		this.title = title;
		this.content = content;
		this.maxMember = maxMember;
	}
	
	private static void maxMemberCheck(int maxMember) {
		if (maxMember<1) {
			throw new IllegalArgumentException("최대 인원은 1명 이상이어야 합니다.");
		}
	}

	public void closeRecruitment() {
		this.status = StudyStatus.CLOSED;
	}
}
