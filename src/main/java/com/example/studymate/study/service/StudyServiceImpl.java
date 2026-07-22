package com.example.studymate.study.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.dto.StudyListResponseDto;
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

		Member creator = memberRepository.findById(memberId).orElseThrow(
				() -> new NoSuchElementException("회원을 찾을 수 없습니다.")
		);

		Study study = Study.create(request.getTitle(), request.getContent(), request.getMaxMember(), creator);

		Study savedStudy = studyRepository.save(study);

		return StudyResponseDto.from(savedStudy);
	}

	@Override
    @Transactional(readOnly = true) // db 조회만 수행하는 트랜잭
	public Page<StudyListResponseDto> getStudies(String keyword, Pageable pageable) {
		
		Page<Study> studies;
		
		if (StringUtils.hasText(keyword)) { // 키워드가 null이나 공백인지 확인
			studies = studyRepository.findByTitleContainingIgnoreCase(keyword, pageable);
		}
		else { // 검색어가 없으면 전체 목록을 조회
			studies = studyRepository.findAll(pageable);
		}
		
		return studies.map(StudyListResponseDto::from);
	}

	@Override
    @Transactional(readOnly = true)
	public StudyResponseDto getStudy(Integer studyId) {

		Study study = studyRepository.findById(studyId).orElseThrow(
				() -> new NoSuchElementException( "스터디를 찾을 수 없습니다.")
		);

        return StudyResponseDto.from(study);
	}
}
