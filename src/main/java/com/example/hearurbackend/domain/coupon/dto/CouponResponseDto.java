package com.example.hearurbackend.domain.coupon.dto;

import com.example.hearurbackend.domain.coupon.entity.Coupon;
import com.example.hearurbackend.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class CouponResponseDto {
    private String code;
    private String expirationDate;
    private boolean isUsed;
    private LocalDateTime usedAt;
    private String username;

    public CouponResponseDto(Coupon coupon) {
        this.code = coupon.getCode();
        this.expirationDate = coupon.getExpirationDate().toString();
        this.isUsed = coupon.isUsed();
        this.usedAt = coupon.getUsedAt();
        this.username = Optional.ofNullable(coupon.getUser()).map(User::getUsername).orElse("null_value");
    }
}
