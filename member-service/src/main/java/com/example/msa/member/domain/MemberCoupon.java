package com.example.msa.member.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_coupon")
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    // getters/setters
}
