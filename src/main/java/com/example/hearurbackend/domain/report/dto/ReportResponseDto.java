package com.example.hearurbackend.domain.report.dto;


import com.example.hearurbackend.domain.util.DocsType;
import com.example.hearurbackend.domain.report.entity.Report;
import lombok.Getter;

@Getter
public class ReportResponseDto {
    private Long id;
    private String username;
    private String reportDate;
    private String description;
    private DocsType docsType;
    private String status;
    private String type;
    private String answer;

    public ReportResponseDto(Report report) {
        this.id = report.getId();
        this.username = report.getReporter().getUsername();
        this.reportDate = report.getReportDate().toString();
        this.description = report.getDescription();
        this.status = report.getStatus().toString();
        this.type = report.getType().toString();
        this.docsType = report.getDocsType();
        this.answer = report.getAnswer();
    }
}
