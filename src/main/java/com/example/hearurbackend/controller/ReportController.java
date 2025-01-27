package com.example.hearurbackend.controller;

import com.example.hearurbackend.domain.oauth.dto.CustomOAuth2User;
import com.example.hearurbackend.domain.report.dto.ReportProcessRequestDto;
import com.example.hearurbackend.domain.report.dto.ReportRequestDto;
import com.example.hearurbackend.domain.report.dto.ReportResponseDto;
import com.example.hearurbackend.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ReportController {
    private final ReportService reportService;
    @Operation(summary = "신고")
    @PostMapping("/report")
    public ResponseEntity<Void> report(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody ReportRequestDto reportRequestDto
    ) {
        reportService.report(auth.getUsername(), reportRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "신고 목록 조회")
    @GetMapping("/report")
    public ResponseEntity<List<ReportResponseDto>> getReport(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(reportService.getReportList(auth.getUsername()));
    }

    @Operation(summary = "신고 상세 조회")
    @GetMapping("/report/{id}")
    public ResponseEntity<ReportResponseDto> getReportDetail(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reportService.getReportDetail(auth.getUsername(), id));
    }

    @Operation(summary = "신고 처리")
    @PutMapping("/report/{id}")
    public ResponseEntity<Void> processReport(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable Long id,
            @RequestBody ReportProcessRequestDto reportProcessRequestDto
    ) {
        reportService.processReport(auth.getUsername(), id, reportProcessRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary="내 신고 목록 조회")
    @GetMapping("/report/my")
    public ResponseEntity<List<ReportResponseDto>> getMyReport(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(reportService.getMyReportList(auth.getUsername()));
    }

    @Operation(summary="내 신고 목록(ask) 조회")
    @GetMapping("/report/my/ask")
    public ResponseEntity<List<ReportResponseDto>> getMyReportAsk(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(reportService.getMyReportListAsk(auth.getUsername()));
    }
}
