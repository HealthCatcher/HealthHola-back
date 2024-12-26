package com.example.hearurbackend.dto.survey;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateQuestionRequestDto {
    String title;
    List<String> answerOptions;
}
