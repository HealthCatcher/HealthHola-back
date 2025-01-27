package com.example.hearurbackend.domain.report.dto;

import com.example.hearurbackend.domain.util.DocsType;
import com.example.hearurbackend.domain.report.type.ReportType;
import lombok.Getter;

@Getter
public class ReportRequestDto {
    private ReportType type;
    private DocsType docsType;
    private String description;
    private String reportDate;
    private String id;
    private String comment;
    private String status;
}
