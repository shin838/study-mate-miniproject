package com.example.demo.member.repository;

import com.example.demo.member.entity.Role;
import com.example.demo.member.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}
