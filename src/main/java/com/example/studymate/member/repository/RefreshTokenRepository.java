package com.example.studymate.member.repository;

import com.example.studymate.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    
    
    
}
