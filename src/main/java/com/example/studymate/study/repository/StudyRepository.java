package com.example.studymate.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studymate.study.entity.Study;

public interface StudyRepository extends JpaRepository<Study, Integer>{

}
