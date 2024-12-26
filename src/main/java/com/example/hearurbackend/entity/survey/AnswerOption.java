package com.example.hearurbackend.entity.survey;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class AnswerOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String answer;
    private Integer index;

    public AnswerOption(Question question, String answer, Integer index) {
        this.question = question;
        this.answer = answer;
        this.index = index;
    }
}
