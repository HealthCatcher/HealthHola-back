package com.example.hearurbackend.dto.experience;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeRequestDto {
    private String category;
    private String title;
    private String content;
    private String author;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String campaignDetails;
    private String instruction;
}
