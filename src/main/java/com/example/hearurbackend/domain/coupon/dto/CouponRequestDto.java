package com.example.hearurbackend.domain.coupon.dto;

import com.example.hearurbackend.domain.coupon.type.CouponType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CouponRequestDto {
    private String couponCode;
    private LocalDateTime expirationDate;
    private CouponType type;
}