package com.example.studymate.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studymate.member.entity.Member;

public interface AdminMemberRepository extends JpaRepository<Member, Integer> {
}
