package com.example.hearurbackend.dto.report;

import com.example.hearurbackend.domain.ReportStatus;
import lombok.Getter;

@Getter
public class ReportProcessRequestDto {
    private String status;
    private String answer;
}
