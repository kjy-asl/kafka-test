package com.example.msa.member.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate birthday;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // getters/setters
}
