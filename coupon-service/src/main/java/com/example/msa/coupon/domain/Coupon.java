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

    public Long getId() {
        return id;
    }

    public CouponTemplate getTemplate() {
        return template;
    }

    public void setTemplate(CouponTemplate template) {
        this.template = template;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
