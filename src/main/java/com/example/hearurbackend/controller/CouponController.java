package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.coupon.CouponRequestDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupon")
public class CouponController {
    private final CouponService couponService;

    @Operation(summary = "쿠폰 사용")
    @PutMapping("/{coupon_code}")
    public void useCoupon(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable String coupon_code) {
        couponService.useCoupon(coupon_code, auth.getUsername());
    }
    @Operation(summary = "쿠폰 추가")
    @PostMapping
    public void addCoupon(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody CouponRequestDto couponDto) {
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        couponService.addCoupon(auth, couponDto.getCode(), couponDto.getExpirationDate());
    }

    @Operation(summary = "쿠폰 삭제")
    @DeleteMapping("/{coupon_code}")
    public void deleteCoupon(
            @PathVariable String coupon_code,
            @AuthenticationPrincipal CustomOAuth2User auth) {
        couponService.deleteCoupon(auth, coupon_code);
    }
}
