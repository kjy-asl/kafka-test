package com.example.msa.coupon.api;

import com.example.msa.coupon.repository.CouponRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
public class CouponQueryController {

    private final CouponRepository couponRepository;

    public CouponQueryController(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCoupons(@RequestParam Long memberId) {
        return ResponseEntity.ok(couponRepository.findByMemberId(memberId));
    }
}
