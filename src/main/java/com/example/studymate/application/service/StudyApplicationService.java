package com.example.studymate.application.service;

import java.util.List;

import com.example.studymate.application.entity.StudyApplication;

public interface StudyApplicationService {

    // 스터디 참여 신청
    StudyApplication applyStudy(Integer memberId, Integer studyId);

    // 참여 취소
    StudyApplication cancelApplication(Integer memberId, Integer studyId);

    // mypage 영역에서 담당
    // 내가 참여한 스터디 조회
//    List<StudyApplication> listMyApplications(Integer memberId);
}