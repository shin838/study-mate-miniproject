package com.example.studymate.study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "leader_id", nullable = false)
//	private Member leader;

	private Study(
            String title,
            String content,
            int maxMember
//            Member leader
    ) {
        this.title = title;
        this.content = content;
        this.maxMember = maxMember;
//        this.leader = leader;
        this.status = StudyStatus.RECRUITING;
    }

    public static Study create(
            String title,
            String content,
            int maxMember
//            Member leader
    ) {
        return new Study(title, content, maxMember); // leader
    }	
	
}
