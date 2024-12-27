package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.coupon.CouponRequestDto;
import com.example.hearurbackend.dto.coupon.CouponResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CouponController {
    private final CouponService couponService;
    @Operation(summary = "쿠폰 사용")
    @PutMapping("/coupon/{coupon_code}")
    public ResponseEntity<Void> useCoupon(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable String coupon_code
    ) {
        couponService.useCoupon(coupon_code, auth.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "쿠폰 삭제")
    @DeleteMapping("/coupon/{coupon_code}")
    public ResponseEntity<Void> deleteCoupon(
            @PathVariable String coupon_code,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        couponService.deleteCoupon(auth, coupon_code);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "쿠폰 조회")
    @GetMapping("/coupon")
    public ResponseEntity<List<CouponResponseDto>> getCoupon(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(couponService.getCouponList(auth));
    }

    @Operation(summary = "쿠폰 추가")
    @PostMapping("/coupon")
    public ResponseEntity<Void> addCoupon(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody CouponRequestDto couponRequestDto
    ) {
        couponService.addCoupon(auth, couponRequestDto);
        return ResponseEntity.ok().build();
    }
}
