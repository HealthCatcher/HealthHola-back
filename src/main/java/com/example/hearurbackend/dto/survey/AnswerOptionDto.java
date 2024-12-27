package com.example.hearurbackend.dto.survey;

import com.example.hearurbackend.entity.survey.AnswerOption;
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

