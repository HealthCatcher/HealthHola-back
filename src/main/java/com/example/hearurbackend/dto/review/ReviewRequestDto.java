package com.example.hearurbackend.dto.review;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewRequestDto {
    String content;
    List<String> urls;
}
