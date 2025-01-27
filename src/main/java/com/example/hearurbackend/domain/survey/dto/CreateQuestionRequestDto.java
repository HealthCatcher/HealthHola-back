package com.example.hearurbackend.domain.survey.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateQuestionRequestDto {
    String title;
    List<String> answerOptions;
}
