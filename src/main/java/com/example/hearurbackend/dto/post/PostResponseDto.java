package com.example.hearurbackend.dto.post;

import com.example.hearurbackend.dto.comment.CommentResponseDto;
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
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final boolean isUpdated;
    private final List<CommentResponseDto> comments;
    private final int views;
    private final int likes;
    private final int commentsCount;
    private final boolean isLiked;
    private final String imageUrl;

    @Builder
    public PostResponseDto(Long no, String category, String title, String content, String author, LocalDateTime createDate, LocalDateTime updateDate, boolean isUpdated, List<CommentResponseDto> comments, int views, int likes, int commentsCount, boolean isLiked, String imageUrl) {
        this.no = no;
        this.category = category;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.isUpdated = isUpdated;
        this.comments = comments;
        this.views = views;
        this.likes = likes;
        this.commentsCount = commentsCount;
        this.isLiked = isLiked;
        this.imageUrl = imageUrl;
    }
}
