package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.entity.coupon.Coupon;
import com.example.hearurbackend.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    // 쿠폰 사용 메서드
    @Transactional
    public void useCoupon(String couponCode, String userId) {
        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다: " + couponCode));

        // 쿠폰이 해당 사용자에게 할당된 쿠폰인지 확인
        if (coupon.getUser() == null || !coupon.getUser().getUsername().equals(userId)) {
            throw new IllegalStateException("해당 쿠폰은 이 사용자에게 할당된 쿠폰이 아닙니다.");
        }

        // 쿠폰 사용 처리
        coupon.useCoupon();

        // 변경 사항 저장
        couponRepository.save(coupon);
    }

    // 쿠폰 추가 메서드
    @Transactional
    public void addCoupon(CustomOAuth2User user, String couponCode, LocalDateTime expirationDate) {

        if(user.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        Coupon coupon = new Coupon(couponCode, expirationDate);
        // 쿠폰 사용자 할당


        // 변경 사항 저장
        couponRepository.save(coupon);
    }

    @Transactional
    public void deleteCoupon(CustomOAuth2User user, String couponCode) {
        if(user.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        Coupon coupon = couponRepository.findById(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다: " + couponCode));

        couponRepository.delete(coupon);
    }
}
