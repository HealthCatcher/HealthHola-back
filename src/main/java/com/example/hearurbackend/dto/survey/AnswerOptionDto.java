package com.example.hearurbackend.dto.survey;

import lombok.Getter;

@Getter
public class AnswerOptionDto {
    private String answer;
    private Integer index;

    public AnswerOptionDto(String answer, Integer index) {
        this.answer = answer;
        this.index = index;
    }
}

