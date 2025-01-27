package com.example.hearurbackend.domain.coupon.repository;

import com.example.hearurbackend.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {
}
