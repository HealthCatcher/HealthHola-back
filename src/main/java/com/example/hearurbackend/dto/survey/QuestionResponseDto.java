package com.example.hearurbackend.dto.survey;

import com.example.hearurbackend.entity.survey.AnswerOption;
import com.example.hearurbackend.entity.survey.Question;
import lombok.Getter;

import java.util.List;

@Getter
public class QuestionResponseDto {
    private Long questionId;
    private String title;
    private List<AnswerOptionDto> answerOptions;

    public QuestionResponseDto(Question question) {
        this.questionId = question.getId();
        this.title = question.getQuestionText();
        this.answerOptions = question.getAnswerOptions().stream()
                .map(option -> new AnswerOptionDto(option.getAnswer(), option.getIndex()))
                .toList();
    }
}
