package com.example.studymate.studypost.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.repository.StudyRepository;
import com.example.studymate.studymember.repository.StudyMemberRepository;
import com.example.studymate.studypost.dto.StudyPostRequestDto;
import com.example.studymate.studypost.dto.StudyPostResponseDto;
import com.example.studymate.studypost.entity.StudyPost;
import com.example.studymate.studypost.repository.StudyPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyPostServiceImpl implements StudyPostService {

	private final StudyPostRepository studyPostRepository;
	private final StudyMemberRepository studyMemberRepository;
	private final StudyRepository studyRepository;
	private final MemberRepository memberRepository;

	private void validateParticipant(Integer studyId, Integer memberId) {
		boolean participating = studyMemberRepository.existsByStudy_StudyIdAndMember_MemberId(studyId, memberId);

		if (!participating) {
			throw new AccessDeniedException("참여 중인 스터디에서만 게시글을 이용할 수 있습니다.");
		}
	}

	@Override
	public StudyPostResponseDto createPost(Integer studyId, Integer memberId, StudyPostRequestDto requestDto) {
		Study study = studyRepository.findById(studyId)
				.orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		validateParticipant(studyId, memberId);

		StudyPost studyPost = StudyPost.create(study, member, requestDto.getContent());

		StudyPost savedPost = studyPostRepository.save(studyPost);

		return StudyPostResponseDto.from(savedPost);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<StudyPostResponseDto> getPosts(Integer studyId, Integer memberId, Pageable pageable) {
		if (!studyRepository.existsById(studyId)) {
			throw new IllegalArgumentException("스터디가 존재하지 않습니다.");
		}

		validateParticipant(studyId, memberId);

		return studyPostRepository.findAllByStudyIdWithMember(studyId, pageable).map(StudyPostResponseDto::from);
	}

	@Override
	public void deletePost(Integer studyId, Integer postId, Integer memberId) {
		validateParticipant(studyId, memberId);

		StudyPost studyPost = studyPostRepository.findByPostIdAndStudy_StudyId(postId, studyId)
				.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

		if (!studyPost.getMember().getMemberId().equals(memberId)) {
			throw new AccessDeniedException("자신이 작성한 게시글만 삭제할 수 있습니다.");
		}

		studyPostRepository.delete(studyPost);
	}

	@Override
	public StudyPostResponseDto updatePost(Integer studyId, Integer postId, Integer memberId,
			StudyPostRequestDto requestDto) {
		
		validateParticipant(studyId, memberId);

		StudyPost studyPost = studyPostRepository.findByPostIdAndStudy_StudyId(postId, studyId)
				.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

		if (!studyPost.getMember().getMemberId().equals(memberId)) {
			throw new AccessDeniedException("자신이 작성한 게시글만 수정할 수 있습니다.");
		}

		studyPost.updateContent(requestDto.getContent());

		return StudyPostResponseDto.from(studyPost);
	}
}