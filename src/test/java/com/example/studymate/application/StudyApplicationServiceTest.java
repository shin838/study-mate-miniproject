package com.example.studymate.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.studymate.application.entity.ApplicationStatus;
import com.example.studymate.application.entity.StudyApplication;
import com.example.studymate.application.repository.StudyApplicationRepository;
import com.example.studymate.application.service.StudyApplicationServiceImpl;
import com.example.studymate.member.entity.Member;
import com.example.studymate.member.repository.MemberRepository;
import com.example.studymate.study.entity.Study;
import com.example.studymate.study.repository.StudyRepository;
import com.example.studymate.studymember.entity.StudyMember;
import com.example.studymate.studymember.entity.StudyRole;
import com.example.studymate.studymember.repository.StudyMemberRepository;

@ExtendWith(MockitoExtension.class) // 이 테스트 클래스 안에서 @Mock, @InjectMocks를 사용할 수 있게 Mockito 기능을 켠다.
public class StudyApplicationServiceTest {

	@Mock // 실제 DB에 접근하지 않고, Repository가 어떤 값을 돌려줄지 테스트에서 직접 정한다.
	private StudyApplicationRepository studyApplicationRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private StudyRepository studyRepository;

	@Mock
	private StudyMemberRepository studyMemberRepository;

	@InjectMocks // 위에서 만든 Mock Repository들을 StudyApplicationServiceImpl에 주입해서 서비스 로직만 테스트한다.
	private StudyApplicationServiceImpl studyApplicationService;

	// 테스트마다 Member 객체를 계속 만들면 코드가 길어지니까 공통 메서드로 분리한다.
	// 실제 DB 회원이 아니라 테스트 안에서만 사용하는 가짜 회원 객체다.
	private Member createMember(Integer memberId) {
		return Member.builder().memberId(memberId).email("test@test.com").password("1234").name("테스트")
				.nickname("tester").build();
	}

	@Test
	void applyStudy_success() {
		// given
		// 테스트에 필요한 가짜 데이터를 준비한다.
		// 실제 DB에 저장하는 것이 아니라 테스트 안에서만 사용할 객체를 직접 만든다.
		Integer memberId = 1;
		Integer studyId = 1;

		Member member = createMember(memberId);

		// Study.create()는 Study 엔티티에 만든 정적 생성 메서드다.
		Study study = Study.create("자바 스터디", "JPA 공부", 3, member);

		// memberRepository.findById(1)을 호출하면 Optional.of(member)를 돌려주라고 설정한다.
		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		// studyRepository.findById(1)을 호출하면 Optional.of(study)를 돌려주라고 설정한다.
		given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

//		// 이 회원이 이 스터디에 이미 신청했는지 물어보면 false라고 대답하게 한다.
//		// false니까 중복 신청이 아닌 상황이다.
//		given(studyApplicationRepository.existsByMemberAndStudy(member, study)).willReturn(false);

		given(studyMemberRepository.existsByStudy_StudyIdAndMember_MemberId(studyId, memberId)).willReturn(false);

		given(studyApplicationRepository.findByMemberAndStudy(member, study)).willReturn(Optional.empty());

//        // 이 스터디에 현재 신청한 사람이 몇 명인지 물어보면 0명이라고 대답하게 한다.
//        // 정원은 3명이고 현재 신청자는 0명이므로 신청 가능하다.
//        given(studyApplicationRepository.countByStudyAndApplicationStatus(study, ApplicationStatus.APPLIED.name()))
//                .willReturn(0);

		given(studyMemberRepository.countByStudy_StudyIdAndStudyRole(studyId, StudyRole.MEMBER)).willReturn(0);

		// save()가 호출되면 DB에 저장하지 않고, save()로 넘어온 StudyApplication 객체를 그대로 다시 반환한다.
		given(studyApplicationRepository.save(any(StudyApplication.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

		// when
		// 실제 테스트 대상인 service의 applyStudy()를 실행한다.
		StudyApplication result = studyApplicationService.applyStudy(memberId, studyId);

		// then
		// 실행 결과가 우리가 기대한 값인지 확인한다.
		assertThat(result.getMember()).isEqualTo(member); // 신청 결과의 회원이 준비한 member와 같은지 확인
		assertThat(result.getStudy()).isEqualTo(study); // 신청 결과의 스터디가 준비한 study와 같은지 확인
		assertThat(result.getApplicationStatus()).isEqualTo(ApplicationStatus.APPLIED.name()); // 신청 상태가 APPLIED인지 확인

		// StudyMemberRepository.save()로 전달된 객체를 가져와 실제 MEMBER 역할로 만들어졌는지 확인한다.
		ArgumentCaptor<StudyMember> captor = ArgumentCaptor.forClass(StudyMember.class);

		verify(studyMemberRepository).save(captor.capture());

		StudyMember savedStudyMember = captor.getValue();

		assertThat(savedStudyMember.getMember()).isEqualTo(member);

		assertThat(savedStudyMember.getStudy()).isEqualTo(study);

		assertThat(savedStudyMember.getStudyRole()).isEqualTo(StudyRole.MEMBER);

		verify(studyMemberRepository).countByStudy_StudyIdAndStudyRole(studyId, StudyRole.MEMBER);
	}

	@Test
	void applyStudy_fail_whenDuplicateApplication() {
		// given
		// 이미 신청한 회원이 다시 같은 스터디에 신청하려는 상황이다.
		Integer memberId = 1;
		Integer studyId = 1;

		Member member = createMember(memberId);
		Study study = Study.create("자바 스터디", "JPA 공부", 3, member);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

		given(studyMemberRepository.existsByStudy_StudyIdAndMember_MemberId(studyId, memberId)).willReturn(true);

		// when & then
		// 중복 신청이면 IllegalStateException이 발생해야 한다.
		assertThatThrownBy(() -> studyApplicationService.applyStudy(memberId, studyId))
				.isInstanceOf(IllegalStateException.class).hasMessage("이미 참여 중인 스터디입니다.");

		verify(studyApplicationRepository, never()).save(any(StudyApplication.class));

		verify(studyMemberRepository, never()).save(any(StudyMember.class));
	}

	@Test
	void applyStudy_fail_whenMaxMemberReached() {
		// given
		// 정원 3명짜리 스터디에 이미 3명이 신청한 상황이다.
		Integer memberId = 1;
		Integer studyId = 1;

		Member member = createMember(memberId);
		Study study = Study.create("자바 스터디", "JPA 공부", 3, member);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
		given(studyMemberRepository.existsByStudy_StudyIdAndMember_MemberId(studyId, memberId)).willReturn(false);

		// 현재 신청 인원이 3명이고 study의 maxMember도 3이므로 더 이상 신청할 수 없다.
//        given(studyApplicationRepository.countByStudyAndApplicationStatus(study, ApplicationStatus.APPLIED.name()))
//                .willReturn(3);
		given(studyMemberRepository.countByStudy_StudyIdAndStudyRole(studyId, StudyRole.MEMBER)).willReturn(3);

		// when & then
		// 정원이 다 찼으므로 신청하면 예외가 발생해야 한다.
		assertThatThrownBy(() -> studyApplicationService.applyStudy(memberId, studyId))
				.isInstanceOf(IllegalStateException.class).hasMessage("모집 인원이 마감되었습니다.");

		// 정원이 찼으므로 두 테이블 모두 저장되면 안 된다.
		verify(studyApplicationRepository, never()).save(any(StudyApplication.class));

		verify(studyMemberRepository, never()).save(any(StudyMember.class));
	}

	@Test
	void cancelApplication_success() {
		// given
		// 회원이 이미 스터디에 신청해둔 상태다.
		Integer memberId = 1;
		Integer creatorId = 99;
		Integer studyId = 1;

		Member member = createMember(memberId);
		Member creator = createMember(creatorId);
		Study study = Study.create("자바 스터디", "JPA 공부", 3, creator);

		StudyApplication application = new StudyApplication();
		application.setMember(member);
		application.setStudy(study);
		application.setApplicationStatus(ApplicationStatus.APPLIED.name());

		// 실제 참여 관계는 MEMBER
		StudyMember studyMember = StudyMember.createMember(study, member);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

		// 신청 내역을 찾으면 위에서 만든 application이 나온다고 설정한다.
		given(studyApplicationRepository.findByMemberAndStudy(member, study)).willReturn(Optional.of(application));

		given(studyMemberRepository.findByStudy_StudyIdAndMember_MemberId(studyId, memberId))
				.willReturn(Optional.of(studyMember));

		// 취소 후 save()가 호출되면 저장된 객체를 그대로 반환하게 설정한다.
		given(studyApplicationRepository.save(any(StudyApplication.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

		// when
		StudyApplication result = studyApplicationService.cancelApplication(memberId, studyId);

		// then
		// 참여 취소 후 상태가 CANCELED로 바뀌었는지 확인한다.
		assertThat(result.getApplicationStatus()).isEqualTo(ApplicationStatus.CANCELED.name());

		verify(studyMemberRepository).deleteByStudy_StudyIdAndMember_MemberId(studyId, memberId);
	}

	@Test // 리더가 취소 요청할 경우 실패
	void cancelApplication_fail_whenLeader() {
		// given
		Integer memberId = 1;
		Integer studyId = 1;

		Member creator = createMember(memberId);

		Study study = Study.create("자바 스터디", "JPA 공부", 3, creator);

		StudyApplication application = new StudyApplication();

		application.setMember(creator);
		application.setStudy(study);
		application.setApplicationStatus(ApplicationStatus.APPLIED.name());

		StudyMember leader = StudyMember.createLeader(study, creator);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(creator));

		given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

		given(studyApplicationRepository.findByMemberAndStudy(creator, study)).willReturn(Optional.of(application));

		given(studyMemberRepository.findByStudy_StudyIdAndMember_MemberId(studyId, memberId))
				.willReturn(Optional.of(leader));

		// when & then
		assertThatThrownBy(() -> studyApplicationService.cancelApplication(memberId, studyId))
				.isInstanceOf(IllegalStateException.class).hasMessage("스터디장은 참여를 취소할 수 없습니다.");

		verify(studyMemberRepository, never()).deleteByStudy_StudyIdAndMember_MemberId(studyId, memberId);
	}

	@Test // 취소 후 재신청 테스트
	void applyStudy_success_whenReapplyAfterCancel() {
		// given
		Integer memberId = 1;
		Integer creatorId = 99;
		Integer studyId = 1;

		Member member = createMember(memberId);
		Member creator = createMember(creatorId);

		Study study = Study.create("자바 스터디", "JPA 공부", 3, creator);

		StudyApplication canceledApplication = new StudyApplication();

		canceledApplication.setMember(member);
		canceledApplication.setStudy(study);
		canceledApplication.setApplicationStatus(ApplicationStatus.CANCELED.name());

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

		given(studyMemberRepository.existsByStudy_StudyIdAndMember_MemberId(studyId, memberId)).willReturn(false);

		given(studyMemberRepository.countByStudy_StudyIdAndStudyRole(studyId, StudyRole.MEMBER)).willReturn(0);

		given(studyApplicationRepository.findByMemberAndStudy(member, study))
				.willReturn(Optional.of(canceledApplication));

		given(studyApplicationRepository.save(any(StudyApplication.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

		// when
		StudyApplication result = studyApplicationService.applyStudy(memberId, studyId);

		// then
		assertThat(result.getApplicationStatus()).isEqualTo(ApplicationStatus.APPLIED.name());

		verify(studyMemberRepository).save(any(StudyMember.class));
	}
}
