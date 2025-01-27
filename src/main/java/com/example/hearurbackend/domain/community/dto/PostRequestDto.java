package com.example.hearurbackend.domain.community.dto;

import lombok.Getter;

@Getter
public class PostRequestDto {
    private String title;
    private String category;
    private String content;
}
