package com.example.hearurbackend.dto.report;

import com.example.hearurbackend.domain.DocsType;
import com.example.hearurbackend.domain.ReportType;
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
