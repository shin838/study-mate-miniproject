package com.example.studymate.member.repository;

import com.example.studymate.entity.Role;
import com.example.studymate.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(RoleName roleName);
    
}
