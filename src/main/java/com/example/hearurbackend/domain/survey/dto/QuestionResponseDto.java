package com.example.hearurbackend.domain.survey.dto;

import com.example.hearurbackend.domain.survey.entity.AnswerOption;
import com.example.hearurbackend.domain.survey.entity.Question;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class QuestionResponseDto {
    private Long questionId;
    private String title;
    private List<AnswerOptionDto> answerOptions;
    private int totalParticipants; // 총 참여자 수
    private boolean isAnswered; // 사용자가 응답했는지 여부
    private int selectedAnswerIndex; // 사용자가 선택한 답변 인덱스

    public QuestionResponseDto(Question question, Map<AnswerOption, Long> optionCounts, int totalParticipants, boolean isAnswered, int selectedAnswerIndex) {
        this.questionId = question.getId();
        this.title = question.getQuestionText();
        this.answerOptions = question.getAnswerOptions().stream()
                .map(option -> new AnswerOptionDto(option, optionCounts.getOrDefault(option, 0L)))
                .toList();
        this.totalParticipants = totalParticipants;
        this.isAnswered = isAnswered;
        this.selectedAnswerIndex = selectedAnswerIndex;
    }
}
