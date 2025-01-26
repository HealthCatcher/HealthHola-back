package com.example.hearurbackend.dto.comment;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class CommentResponseDto {
    private UUID id;
    private UUID parentCommentId;
    private String author;
    private String content;
    private LocalDateTime createDate;
    private boolean isUpdated;
    private List<CommentResponseDto> replies;
    private boolean isReported;

    public CommentResponseDto(UUID id, UUID parentCommentId, String author, String content, LocalDateTime createDate, boolean isUpdated, List<CommentResponseDto> replies, boolean isReported) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.author = author;
        this.content = content;
        this.createDate = createDate;
        this.isUpdated = isUpdated;
        this.replies = replies;
        this.isReported = isReported;
    }
}
