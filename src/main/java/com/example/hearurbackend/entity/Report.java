package com.example.hearurbackend.entity;

import com.example.hearurbackend.domain.DocsType;
import com.example.hearurbackend.domain.ReportStatus;
import com.example.hearurbackend.domain.ReportType;
import com.example.hearurbackend.dto.report.ReportProcessRequestDto;
import com.example.hearurbackend.entity.community.Comment;
import com.example.hearurbackend.entity.community.Post;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.experience.Review;
import com.example.hearurbackend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType type; // 신고 타입 (예: SPAM, INAPPROPRIATE 등)

    @Lob
    private String description; // 신고 상세 내용

    private LocalDateTime reportDate; // 신고 날짜

    private String answer; // 신고 처리 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post; // 관련 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice; // 관련 체험단 공고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment; // 관련 댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review; // 관련 리뷰

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter; // 신고자

    @Enumerated(EnumType.STRING)
    private ReportStatus status; // 신고 처리 상태 (예: PENDING, RESOLVED, DISMISSED)

    @Enumerated(EnumType.STRING)
    private DocsType docsType; // 신고 대상 타입 (예: COMMENT, NOTICE, POST, REVIEW)

    @Builder
    public Report(ReportType type, String description, LocalDateTime reportDate, Post post, Notice notice, Comment comment, Review review, User reporter, ReportStatus status, DocsType docsType) {
        this.type = type;
        this.docsType = docsType;
        this.description = description;
        this.reportDate = reportDate;
        this.post = post;
        this.notice = notice;
        this.comment = comment;
        this.review = review;
        this.reporter = reporter;
        this.status = status;
    }

    public void processReport(ReportProcessRequestDto reportProcessRequestDto) {
        this.answer = reportProcessRequestDto.getAnswer();
        this.status = ReportStatus.valueOf(reportProcessRequestDto.getStatus());
    }
}


