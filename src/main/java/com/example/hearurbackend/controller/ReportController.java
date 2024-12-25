package com.example.hearurbackend.controller;

import com.example.hearurbackend.domain.ReportStatus;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.report.ReportRequestDto;
import com.example.hearurbackend.dto.report.ReportResponseDto;
import com.example.hearurbackend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    @PutMapping("/report/{id}/status/{status}")
    public ResponseEntity<Void> processReport(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @PathVariable Long id,
            @PathVariable String status
    ) {
        ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
        reportService.processReport(auth.getUsername(), id, reportStatus);
        return ResponseEntity.noContent().build();
    }
}
