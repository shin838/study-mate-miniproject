package com.example.studymate.application.service;

import java.util.List;

import com.example.studymate.entity.StudyApplication;

public interface StudyApplicationService {

    // 스터디 참여 신청
    StudyApplication applyStudy(Integer memberId, Integer studyId);

    // 참여 취소
    StudyApplication cancelApplication(Integer memberId, Integer studyId);

    // 내가 참여한 스터디 조회
    List<StudyApplication> listMyApplications(Integer memberId);

    
}// 메서드 만들기
