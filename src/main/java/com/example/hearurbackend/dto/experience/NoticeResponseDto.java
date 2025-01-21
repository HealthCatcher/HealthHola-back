package com.example.hearurbackend.dto.experience;

import com.example.hearurbackend.dto.comment.CommentResponseDto;
import com.example.hearurbackend.dto.review.ReviewResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class NoticeResponseDto {
    private final UUID id;
    private final String category;
    private final String title;
    private final String content;
    private final String author;
    private final LocalDateTime createDate;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final boolean isUpdated;
    private final List<ReviewResponseDto> reviews;
    private final int views;
    private final int likes;
    private final int maxParticipants;
    private final int participants;
    private final List<String> participantsList;
    private final String campaignDetails;
    private final String instruction;
    private final int favoriteCount;
    private final boolean isFavorite;
    private final List<String> titleImageUrls;
    private final List<String> detailImageUrls;
    private final String nextApplyTime;
}
