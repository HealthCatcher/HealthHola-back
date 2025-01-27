package com.example.hearurbackend.domain.survey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AnswerOption> answerOptions;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserResponse> userResponses;

    public Question(String questionText) {
        this.questionText = questionText;
    }
    public Question(String questionText, List<AnswerOption> answerOptions) {
        this.questionText = questionText;
        this.answerOptions = answerOptions;
    }
}
