package com.example.studymate.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studymate.application.entity.ApplicationStatus;
import com.example.studymate.application.entity.StudyApplication;
import com.example.studymate.application.repository.StudyApplicationRepository;
import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.entity.StudyStatus;
import com.example.studymate.study.repository.StudyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyApplicationServiceImpl implements StudyApplicationService {

    private final StudyApplicationRepository studyApplicationRepository;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    @Override
    public StudyApplication applyStudy(Integer memberId, Integer studyId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));

        if (study.getStatus() != StudyStatus.RECRUITING) {
            throw new IllegalStateException("모집 중인 스터디만 신청할 수 있습니다.");
        }

        if (studyApplicationRepository.existsByMemberAndStudy(member, study)) {
            throw new IllegalStateException("이미 신청한 스터디입니다.");
        }

        int appliedCount = studyApplicationRepository.countByStudyAndApplicationStatus(
                study,
                ApplicationStatus.APPLIED.name());

        if (appliedCount >= study.getMaxMember()) {
            throw new IllegalStateException("모집 인원이 마감되었습니다.");
        }

        StudyApplication studyApplication = new StudyApplication();
        studyApplication.setMember(member);
        studyApplication.setStudy(study);
        studyApplication.setApplicationStatus(ApplicationStatus.APPLIED.name());

        return studyApplicationRepository.save(studyApplication);
    }

    @Override
    public StudyApplication cancelApplication(Integer memberId, Integer studyId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));

        StudyApplication studyApplication = studyApplicationRepository.findByMemberAndStudy(member, study)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역이 존재하지 않습니다."));

        studyApplication.setApplicationStatus(ApplicationStatus.CANCELED.name());

        return studyApplicationRepository.save(studyApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyApplication> listMyApplications(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return studyApplicationRepository.findByMember(member);
    }
}
