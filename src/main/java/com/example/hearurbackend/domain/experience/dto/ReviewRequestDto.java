package com.example.hearurbackend.domain.experience.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewRequestDto {
    String content;
    List<String> urls;
}
