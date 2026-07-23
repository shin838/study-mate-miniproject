package com.example.studymate.mypage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studymate.study.entity.Study;

public interface MyStudyRepository extends JpaRepository<Study, Integer> {

    @Query("select s from Study s join fetch s.creator c "
            + "where c.memberId = :memberId order by s.createdAt desc")
    List<Study> findAllByCreatorId(@Param("memberId") Integer memberId);
}
