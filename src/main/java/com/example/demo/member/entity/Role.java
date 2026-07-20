package com.example.demo.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", unique = true)
    private RoleName roleName;
}
