package com.example.hearurbackend.domain.experience.repository;

import com.example.hearurbackend.domain.experience.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByExperienceNoticeId(UUID noticeId);
}
