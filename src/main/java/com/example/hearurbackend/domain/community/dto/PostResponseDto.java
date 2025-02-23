package com.example.hearurbackend.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {
    private final Long no;
    private final String category;
    private final String title;
    private final String content;
    private final String author;
    private final String authorId;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final boolean isUpdated;
    private final List<CommentResponseDto> comments;
    private final int views;
    private final int likes;
    private final int commentsCount;
    private final boolean isLiked;
    private final List<String> imageUrls;
    private final boolean isReported;
    private final boolean isBlocked;

    @Builder
    public PostResponseDto(
            Long no,
            String category,
            String title,
            String content,
            String author,
            String authorId,
            LocalDateTime createDate,
            LocalDateTime updateDate,
            boolean isUpdated,
            List<CommentResponseDto> comments,
            int views,
            int likes,
            int commentsCount,
            boolean isLiked,
            List<String> imageUrls,
            boolean isReported,
            boolean isBlocked
    ) {
        this.no = no;
        this.category = category;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.isUpdated = isUpdated;
        this.comments = comments;
        this.views = views;
        this.likes = likes;
        this.commentsCount = commentsCount;
        this.isLiked = isLiked;
        this.imageUrls = imageUrls;
        this.isReported = isReported;
        this.isBlocked = isBlocked;
    }
}
