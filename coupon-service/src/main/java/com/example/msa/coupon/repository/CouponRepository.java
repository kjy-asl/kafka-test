package com.example.msa.coupon.repository;

import com.example.msa.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByMemberId(Long memberId);
}
