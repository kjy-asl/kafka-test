package com.example.msa.coupon.repository;

import com.example.msa.coupon.domain.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    Optional<CouponTemplate> findByTemplateCode(String templateCode);
}
