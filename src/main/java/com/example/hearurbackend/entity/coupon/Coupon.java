package com.example.hearurbackend.entity.coupon;

import com.example.hearurbackend.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Coupon {

    @Id
    @Column(unique = true, nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // 쿠폰 만료 일자 (사용되지 않을 경우 쿠폰 자체의 만료기한)
    private LocalDateTime expirationDate;

    // 쿠폰 사용 여부
    private boolean isUsed = false;

    // 쿠폰 사용 일자
    private LocalDateTime usedAt;

    // 쿠폰 사용 메서드
    public void useCoupon() {
        if (!isExpired() && !isUsed) {
            this.isUsed = true;
            this.usedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("쿠폰이 만료되었거나 이미 사용된 쿠폰입니다.");
        }
    }

    // 쿠폰 만료 여부 확인 메서드
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
    // 기타 Getter 및 Setter

    public Coupon(String code, LocalDateTime expirationDate){
        this.code = code;
        this.expirationDate = expirationDate;
    }
}
