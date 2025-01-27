package com.example.hearurbackend.domain.survey.dto;

import com.example.hearurbackend.domain.survey.entity.AnswerOption;
import lombok.Getter;

@Getter
public class AnswerOptionDto {
    private String answer;
    private Integer index;
    private Long count;

    public AnswerOptionDto(AnswerOption option, Long count) {
        this.answer = option.getAnswer();
        this.index = option.getIndex();
        this.count = count;
    }
}

