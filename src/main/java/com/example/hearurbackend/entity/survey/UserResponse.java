package com.example.hearurbackend.entity.survey;

import com.example.hearurbackend.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class UserResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_option_id")
    private AnswerOption selectedOption;

    public UserResponse(User user, Question question, Integer answerIndex) {
        this.user = user;
        this.question = question;
        this.selectedOption = question.getAnswerOptions().stream()
                .filter(option -> option.getIndex().equals(answerIndex))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid answer index"));
    }

    // Constructors, Getters, and Setters
}