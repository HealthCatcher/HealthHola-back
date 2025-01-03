package com.example.hearurbackend.dto.comment;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CommentRequestDto {
    private UUID id;
    private UUID parentCommentId;
    private String author;
    private String content;
    private LocalDateTime createDate;
    private boolean isUpdated;

    public CommentRequestDto(UUID id, UUID parentCommentId, String author, String content, LocalDateTime createDate, boolean isUpdated) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.author = author;
        this.content = content;
        this.createDate = createDate;
        this.isUpdated = isUpdated;
    }
}
