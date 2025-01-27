package com.example.hearurbackend.domain.survey.service;

import com.example.hearurbackend.domain.user.service.UserService;
import com.example.hearurbackend.domain.oauth.dto.CustomOAuth2User;
import com.example.hearurbackend.domain.survey.dto.CreateQuestionRequestDto;
import com.example.hearurbackend.domain.survey.dto.QuestionResponseDto;
import com.example.hearurbackend.domain.survey.entity.AnswerOption;
import com.example.hearurbackend.domain.survey.entity.Question;
import com.example.hearurbackend.domain.survey.entity.UserResponse;
import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.survey.repository.AnswerOptionRepository;
import com.example.hearurbackend.domain.survey.repository.QuestionRepository;
import com.example.hearurbackend.domain.survey.repository.SurveyRepository;
import com.example.hearurbackend.domain.survey.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;
    private final UserResponseRepository userResponseRepository;
    private final UserService userService;

    @Transactional
    public void createQuestion(String username, CreateQuestionRequestDto createQuestionRequestDto) {
        if (!userService.isUserAdmin(username)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        Question newQuestion = new Question(createQuestionRequestDto.getTitle());
        Question question = questionRepository.save(newQuestion);
        List<AnswerOption> options = new ArrayList<>();
        for (int i = 0; i < createQuestionRequestDto.getAnswerOptions().size(); i++) {
            options.add(new AnswerOption(question, createQuestionRequestDto.getAnswerOptions().get(i), i));
        }
        answerOptionRepository.saveAll(options);
    }

    public void answerQuestion(String username, Long questionId, Integer answerIndex) {
        log.info("answerIndex: {}", answerIndex);
        User user = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("Question not found with id: " + questionId)
        );
        if (userResponseRepository.existsByUserAndQuestion(user, question)) {
            throw new IllegalArgumentException("이미 응답한 질문입니다.");
        }
        UserResponse userResponse = new UserResponse(user, question, answerIndex);
        log.info("userResponse answerIndex: {}", userResponse.getSelectedOption().getIndex());
        userResponseRepository.save(userResponse);
    }

    public List<QuestionResponseDto> getQuestionList(CustomOAuth2User auth) {
        return questionRepository.findAll().stream()
                .map(question -> {
                    boolean isAnswered = question.getUserResponses().stream()
                            .anyMatch(response -> response.getUser().getUsername().equals(auth.getUsername()));
                    int selectedUserAnswerIndex = question.getUserResponses().stream()
                            .filter(response -> response.getUser().getUsername().equals(auth.getUsername()))
                            .map(UserResponse::getSelectedOption)
                            .map(AnswerOption::getIndex)
                            .findFirst()
                            .orElse(-1);
                    int totalParticipants = question.getUserResponses().size();
                    Map<AnswerOption, Long> optionCounts = question.getUserResponses().stream()
                            .collect(Collectors.groupingBy(UserResponse::getSelectedOption, Collectors.counting()));
                    return new QuestionResponseDto(question, optionCounts, totalParticipants, isAnswered, selectedUserAnswerIndex);
                })
                .toList();
    }


    public void deleteQuestion(String username, Long questionId) {
        if (!userService.isUserAdmin(username)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("Question not found with id: " + questionId)
        );
        questionRepository.delete(question);
    }

    public QuestionResponseDto getQuestionDetail(Long questionId, CustomOAuth2User auth) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("Question not found with id: " + questionId)
        );
        boolean isAnswered = question.getUserResponses().stream()
                .anyMatch(response -> response.getUser().getUsername().equals(auth.getUsername()));
        int selectedUserAnswerIndex = question.getUserResponses().stream()
                .filter(response -> response.getUser().getUsername().equals(auth.getUsername()))
                .map(UserResponse::getSelectedOption)
                .map(AnswerOption::getIndex)
                .findFirst()
                .orElse(-1);
        int totalParticipants = question.getUserResponses().size();
        Map<AnswerOption, Long> optionCounts = question.getUserResponses().stream()
                .collect(Collectors.groupingBy(UserResponse::getSelectedOption, Collectors.counting()));
        return new QuestionResponseDto(question, optionCounts, totalParticipants, isAnswered, selectedUserAnswerIndex);
    }
}
