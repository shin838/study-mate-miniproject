package com.example.studymate.study.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.dto.StudyRequestDto;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.repository.StudyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

	private final StudyRepository studyRepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public StudyResponseDto createStudy(StudyRequestDto request, Integer memberId) {

		Member creator = memberRepository.findById(memberId).orElseThrow();

		Study study = Study.create(request.getTitle(), request.getContent(), request.getMaxMember(), creator);

		Study savedStudy = studyRepository.save(study);

		return StudyResponseDto.from(savedStudy);
	}
}
