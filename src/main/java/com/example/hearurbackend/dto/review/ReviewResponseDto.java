package com.example.hearurbackend.dto.review;

import com.example.hearurbackend.entity.experience.Review;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ReviewResponseDto {
    private UUID id;
    private String author;
    private String content;
    private LocalDateTime createDate;

    public ReviewResponseDto(UUID id, String author, String content, LocalDateTime createDate) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.createDate = createDate;
    }

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.author = review.getUser().getUsername();
        this.content = review.getContent();
        this.createDate = review.getCreatedAt();
    }
}
