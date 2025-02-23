package com.example.hearurbackend.domain.coupon.service;

import com.example.hearurbackend.domain.user.service.UserService;
import com.example.hearurbackend.domain.coupon.dto.CouponRequestDto;
import com.example.hearurbackend.domain.coupon.dto.CouponResponseDto;
import com.example.hearurbackend.domain.oauth.dto.CustomOAuth2User;
import com.example.hearurbackend.domain.coupon.entity.Coupon;
import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.coupon.repository.CouponRepository;
import com.example.hearurbackend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    // 쿠폰 사용 메서드
    @Transactional
    public void useCoupon(String couponCode, String userId) {
        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다: " + couponCode));
        // 쿠폰 사용 처리
        User user = userService.getUser(userId).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + userId));
        if (user.isCouponUsed()) {
            throw new IllegalStateException("이미 쿠폰 사용자입니다.");
        }
        validateCoupon(coupon);
        switch (coupon.getType()) {
            case PREMIUM:
                user.usePremiumCoupon();
                break;
            case PRIORITY:
                user.usePriorityCoupon();
                break;
        }
        coupon.useCoupon(user);
        userRepository.save(user);
        couponRepository.save(coupon);
    }

    // 쿠폰 추가 메서드
    @Transactional
    public void addCoupon(CustomOAuth2User user, CouponRequestDto couponDto) {
        if(user.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        log.info("쿠폰 추가 요청 getCode{}", couponDto.getCouponCode());
        log.info("쿠폰 추가 요청 getExpirationDate{}", couponDto.getExpirationDate());
        Coupon coupon = new Coupon(couponDto.getCouponCode(), couponDto.getExpirationDate(), couponDto.getType());
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

    public List<CouponResponseDto> getCouponList(CustomOAuth2User auth) {
        if(auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        return couponRepository.findAll().stream()
                .map(CouponResponseDto::new)
                .toList();
    }
    private void validateCoupon(Coupon coupon) {
        if (coupon.isExpired() || coupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰이거나 만료된 쿠폰입니다.");
        }
    }
}
