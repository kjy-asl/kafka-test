package com.example.msa.coupon.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons", uniqueConstraints = {
        @UniqueConstraint(name = "uk_coupon_unique", columnNames = {"member_id", "template_id", "correlation_id"})
})
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private CouponTemplate template;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "status", nullable = false)
    private String status;

    // getters/setters
}
