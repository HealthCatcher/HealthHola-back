package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.experience.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByExperienceNoticeId(UUID noticeId);
}
