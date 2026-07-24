package com.example.studymate.mypage.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.mypage.dto.MyParticipatingStudyDetailResponseDto;
import com.example.studymate.mypage.dto.MyParticipatingStudyResponseDto;
import com.example.studymate.mypage.repository.MyStudyRepository;
import com.example.studymate.study.dto.StudyResponseDto;
import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.repository.StudyMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyStudyService {

    private final MyStudyRepository myStudyRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional(readOnly = true)
    public List<StudyResponseDto> getMyStudies(Integer memberId) {
        return myStudyRepository.findAllByCreatorId(memberId).stream()
                .map(StudyResponseDto::from)
                .toList();
    }
    
    // 내가 실제로 참여 중인 스터디 목록
    @Transactional(readOnly = true)
    public List<MyParticipatingStudyResponseDto> getMyParticipatingStudies(Integer memberId) {

        return studyMemberRepository
                .findAllByMemberIdWithStudy(memberId)
                .stream()
                .map(MyParticipatingStudyResponseDto::from)
                .toList();
    }
    
    // 내가 참여 중인 스터디 상세조회
    @Transactional(readOnly = true)
    public MyParticipatingStudyDetailResponseDto
            getMyParticipatingStudyDetail(Integer memberId,Integer studyId) {

        // 로그인한 사용자가 해당 스터디의 실제 참여자인지 확인
        StudyMember myStudyMember = studyMemberRepository
                        				.findByStudy_StudyIdAndMember_MemberId(studyId,memberId)
                        .orElseThrow(() -> new AccessDeniedException("참여 중인 스터디만 조회할 수 있습니다."));

        // 해당 스터디의 LEADER와 MEMBER를 모두 조회
        List<StudyMember> studyMembers = studyMemberRepository
                        					.findAllByStudyIdWithMember(studyId);

        return MyParticipatingStudyDetailResponseDto.from(
                myStudyMember.getStudy(),
                myStudyMember.getStudyRole(),
                studyMembers
        );
    }
}
