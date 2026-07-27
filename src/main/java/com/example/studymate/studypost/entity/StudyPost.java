package com.example.studymate.studypost.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.studymate.member.entity.Member;
import com.example.studymate.study.entity.Study;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_post", indexes = {
		@Index(name = "idx_study_post_study_created", columnList = "study_id, created_at") })
@Getter
@NoArgsConstructor
public class StudyPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Integer postId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_id", nullable = false)
	private Study study;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Lob
	@Column(nullable = false)
	private String content;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private StudyPost(Study study, Member member, String content) {

		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("게시글 내용은 비어 있을 수 없습니다.");
		}
		
		this.study = study;
		this.member = member;
		this.content = content;
	}

	public static StudyPost create(Study study, Member member, String content) {
		return new StudyPost(study, member, content);
	}

	public void updateContent(String content) {
	    
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("게시글 내용은 비어 있을 수 없습니다.");
		}

		this.content = content;
	}
	
}