package com.example.hearurbackend.domain.experience.service;

import com.example.hearurbackend.domain.user.service.UserService;
import com.example.hearurbackend.domain.experience.dto.ReviewRequestDto;
import com.example.hearurbackend.domain.experience.dto.ReviewResponseDto;
import com.example.hearurbackend.domain.experience.entity.Review;
import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.experience.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final NoticeService noticeService;
    public Review getReview(UUID uuid) {
        return reviewRepository.findById(uuid).orElseThrow(
                () -> new IllegalArgumentException("Review not found with id: " + uuid)
        );
    }

    public Review createReview(String username, UUID experienceId, ReviewRequestDto reviewRequestDto) {
        User user = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );

        var notice = noticeService.getNotice(experienceId);

        Review newReview = new Review(user, notice, reviewRequestDto);
        return reviewRepository.save(newReview);
    }

    public void updateReview(String username, UUID experienceId, UUID reviewId, ReviewRequestDto reviewRequestDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("Review not found with id: " + reviewId)
        );

        if (!review.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not the author of this review");
        }

        review.updateReview(reviewRequestDto.getContent(), reviewRequestDto.getUrls());
        reviewRepository.save(review);
    }

    public ReviewResponseDto getReviewDetail(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("Review not found with id: " + reviewId)
        );

        return new ReviewResponseDto(review);
    }

    public void deleteReview(String username, UUID noticeId, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("Review not found with id: " + reviewId)
        );

        if (!review.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not the author of this review");
        }

        reviewRepository.delete(review);
    }

    @Transactional
    public List<ReviewResponseDto> getReviewList(UUID noticeId) {
        List<Review> reviews = reviewRepository.findAllByExperienceNoticeId(noticeId);
        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean checkReviewReported(Review review) {
        return !review.getReports().isEmpty();
    }
}
