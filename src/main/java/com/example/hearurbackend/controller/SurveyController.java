package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.survey.CreateQuestionRequestDto;
import com.example.hearurbackend.dto.survey.QuestionResponseDto;
import com.example.hearurbackend.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
public class SurveyController {
    private final SurveyService surveyService;
    @Operation(summary = "설문조사 생성")
    @PostMapping
    public ResponseEntity<Void> createSurvey() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "질문 생성")
    @PostMapping("/question")
    public ResponseEntity<Void> createQuestion(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody CreateQuestionRequestDto createQuestionRequestDto
    ) {
        surveyService.createQuestion(auth.getUsername(), createQuestionRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "질문 응답")
    @PostMapping("/question/{questionId}/answer/{answerIndex}")
    public ResponseEntity<Void> answerQuestion(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable Long questionId,
            @PathVariable Integer answerIndex
    ) {
        surveyService.answerQuestion(auth.getUsername(), questionId, answerIndex);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "질문 전체 조회")
    @GetMapping("/question")
    public ResponseEntity<List<QuestionResponseDto>> getQuestion(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        List<QuestionResponseDto> QuestionList = surveyService.getQuestionList(auth);
        return ResponseEntity.ok(QuestionList);
    }

    @Operation(summary = "질문 상세 조회")
    @GetMapping("/question/{questionId}")
    public ResponseEntity<QuestionResponseDto> getQuestionDetail(
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        QuestionResponseDto responseDTO = surveyService.getQuestionDetail(questionId,auth);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "질문 삭제")
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable Long questionId
    ) {
        surveyService.deleteQuestion(auth.getUsername(), questionId);
        return ResponseEntity.ok().build();
    }
}
