package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.review.ReviewRequestDto;
import com.example.hearurbackend.dto.review.ReviewResponseDto;
import com.example.hearurbackend.entity.experience.Review;
import com.example.hearurbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experience")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성")
    @PostMapping("/{noticeId}/review")
    public ResponseEntity<Void> createReview(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable UUID noticeId,
            @RequestBody ReviewRequestDto reviewRequestDto

    ) {
        Review newReview = reviewService.createReview(auth.getUsername(), noticeId, reviewRequestDto);
        String reviewId = newReview.getId().toString();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/experience/{noticeId}/review/{reviewId}")
                .buildAndExpand(noticeId, reviewId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/{noticeId}/review/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable UUID noticeId,
            @PathVariable UUID reviewId,
            @RequestBody ReviewRequestDto reviewRequestDto,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        reviewService.updateReview(auth.getUsername(), noticeId, reviewId, reviewRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "리뷰 상세 조회")
    @GetMapping("/{noticeId}/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(
            @PathVariable UUID noticeId,
            @PathVariable UUID reviewId
    ) {
        ReviewResponseDto responseDTO = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "리뷰 목록 조회")
    @GetMapping("/{noticeId}/review")
    public ResponseEntity<List<ReviewResponseDto>> getReviewList(
            @PathVariable UUID noticeId
    ) {
        List<ReviewResponseDto> reviewList = reviewService.getReviewList(noticeId);
        return ResponseEntity.ok(reviewList);
    }


    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{noticeId}/review/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID noticeId,
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        reviewService.deleteReview(auth.getUsername(), noticeId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
