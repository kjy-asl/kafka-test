package com.example.msa.member.service;

import com.example.msa.common.events.CouponIssuedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import com.example.msa.member.domain.MemberCoupon;
import com.example.msa.member.repository.MemberCouponRepository;
import com.example.msa.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberCouponService {

    private static final Logger log = LoggerFactory.getLogger(MemberCouponService.class);

    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;

    public MemberCouponService(MemberRepository memberRepository,
                               MemberCouponRepository memberCouponRepository) {
        this.memberRepository = memberRepository;
        this.memberCouponRepository = memberCouponRepository;
    }

    @Transactional
    public void assignCoupon(CouponIssuedEvent event) {
        var member = memberRepository.findById(event.memberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        MemberCoupon coupon = MemberCoupon.assigned(member, event.couponId());
        memberCouponRepository.save(coupon);

        log.info("[member-service] CouponIssued 수신 member={} coupon={} correlation={}",
                event.memberId(), event.couponId(), event.correlationId());
    }
}
