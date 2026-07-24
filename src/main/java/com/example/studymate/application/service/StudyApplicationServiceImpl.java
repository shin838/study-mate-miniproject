package com.example.studymate.application.service;

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
import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;
import com.example.studymate.studymember.repository.StudyMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyApplicationServiceImpl implements StudyApplicationService {

	private final StudyApplicationRepository studyApplicationRepository;
	private final MemberRepository memberRepository;
	private final StudyRepository studyRepository;

	// 추가
	private final StudyMemberRepository studyMemberRepository;

	@Override
	public StudyApplication applyStudy(Integer memberId, Integer studyId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		Study study = studyRepository.findById(studyId)
				.orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));

		if (study.getStatus() != StudyStatus.RECRUITING) {
			throw new IllegalStateException("모집 중인 스터디만 신청할 수 있습니다.");
		}

		if (studyMemberRepository.existsByStudy_StudyIdAndMember_MemberId(studyId, memberId)) {
			throw new IllegalStateException("이미 참여 중인 스터디입니다.");
		}
		
		int memberCount = studyMemberRepository.countByStudy_StudyIdAndStudyRole(studyId, StudyRole.MEMBER);

		if (memberCount >= study.getMaxMember()) {
			throw new IllegalStateException("모집 인원이 마감되었습니다.");
		}
		
		// 과거 신청 내역을 조회한다.
		StudyApplication studyApplication = studyApplicationRepository.findByMemberAndStudy(member, study).orElse(null);

		if (studyApplication != null) {
			 //APPLIED 신청이 남아 있는데 StudyMember가 없는 경우다. 데이터가 불일치한 상태이므로 중복 신청으로 차단한다.
			if (ApplicationStatus.APPLIED.name().equals(studyApplication.getApplicationStatus())) {
				throw new IllegalStateException("이미 신청한 스터디입니다.");
			}
			

			// 기존 신청이 CANCELED라면 새로운 행을 만들지 않고 기존 행의 상태를 APPLIED로 되돌린다.
			studyApplication.setApplicationStatus(ApplicationStatus.APPLIED.name());
		} else {
			
			// 한 번도 신청한 적이 없다면 새 신청 행을 만든다.
			studyApplication = new StudyApplication();
			studyApplication.setMember(member);
			studyApplication.setStudy(study);
			studyApplication.setApplicationStatus(ApplicationStatus.APPLIED.name());
		}
				
		StudyApplication savedApplication = studyApplicationRepository.save(studyApplication);

		// 실제 참여 관계를 MEMBER 역할로 생성
		StudyMember studyMember = StudyMember.createMember(study, member);

		studyMemberRepository.save(studyMember);

		// maxMember는 리더를 제외한 일반 MEMBER 정원
		if (memberCount + 1 >= study.getMaxMember()) {
			study.closeRecruitment();
		}

		return savedApplication;
	}

	@Override
	public StudyApplication cancelApplication(Integer memberId, Integer studyId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		Study study = studyRepository.findById(studyId)
				.orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));

		StudyApplication studyApplication = studyApplicationRepository.findByMemberAndStudy(member, study)
				.orElseThrow(() -> new IllegalArgumentException("신청 내역이 존재하지 않습니다."));

		// 신청 기록 뿐만 아니라 실제 참여 관계도 확인
		StudyMember studyMember = studyMemberRepository.findByStudy_StudyIdAndMember_MemberId(studyId, memberId)
				.orElseThrow(() -> new IllegalStateException("현재 참여 중인 스터디가 아닙니다."));

		// 리더는 일반 참여 취소로 나갈 수 없다. (리더 위임 후 탈퇴해야함)
		if (studyMember.getStudyRole() == StudyRole.LEADER) {
			throw new IllegalStateException("스터디장은 참여를 취소할 수 없습니다.");
		}

		studyApplication.setApplicationStatus(ApplicationStatus.CANCELED.name());

		// 실제 참여 관계 삭제
		studyMemberRepository.deleteByStudy_StudyIdAndMember_MemberId(studyId, memberId);

		return studyApplicationRepository.save(studyApplication);
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<StudyApplication> listMyApplications(Integer memberId) {
//		Member member = memberRepository.findById(memberId)
//				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
//
//		return studyApplicationRepository.findByMember(member);
//	}
}
