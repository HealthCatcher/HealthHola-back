package com.example.hearurbackend.dto.report;


import com.example.hearurbackend.domain.DocsType;
import com.example.hearurbackend.entity.Report;

public class ReportResponseDto {
    private Long id;
    private String username;
    private String reportDate;
    private String description;
    private DocsType docsType;
    private String status;
    private String type;

    public ReportResponseDto(Report report) {
        this.id = report.getId();
        this.username = report.getReporter().getUsername();
        this.reportDate = report.getReportDate().toString();
        this.description = report.getDescription();
        this.status = report.getStatus().toString();
        this.type = report.getType().toString();
        this.docsType = report.getDocsType();
    }
}
