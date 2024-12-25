package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.coupon.CouponRequestDto;
import com.example.hearurbackend.dto.coupon.CouponResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.entity.coupon.Coupon;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserService userService;

    // 쿠폰 사용 메서드
    @Transactional
    public void useCoupon(String couponCode, String userId) {
        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다: " + couponCode));
        // 쿠폰 사용 처리
        User user = userService.getUser(userId).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + userId));

        if(coupon.isExpired() || coupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        coupon.useCoupon();
        // 변경 사항 저장
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
        Coupon coupon = new Coupon(couponDto.getCouponCode(), couponDto.getExpirationDate());
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
}
