package com.example.hearurbackend.dto.review;

import com.example.hearurbackend.entity.experience.Review;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class ReviewResponseDto {
    private UUID id;
    private String author;
    private String content;
    private LocalDateTime createDate;
    private List<String> urls;

    public ReviewResponseDto(UUID id, String author, String content, LocalDateTime createDate, List<String> urls) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.createDate = createDate;
        this.urls = urls;
    }

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.author = review.getUser().getNickname();
        this.content = review.getContent();
        this.createDate = review.getCreatedAt();
        this.urls = review.getUrls();
    }
}
