package com.example.hearurbackend.service;

import com.example.hearurbackend.domain.ReportStatus;
import com.example.hearurbackend.dto.report.ReportRequestDto;
import com.example.hearurbackend.dto.report.ReportResponseDto;
import com.example.hearurbackend.entity.Report;
import com.example.hearurbackend.entity.community.Comment;
import com.example.hearurbackend.entity.community.Post;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.experience.Review;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final NoticeService experienceService;
    private final PostService postService;
    private final ReviewService reviewService;

    public void report(String username, ReportRequestDto reportRequestDto) {
        // 기본적인 Report 생성 준비
        Report.ReportBuilder reportBuilder = Report.builder()
                .type(reportRequestDto.getType())
                .description(reportRequestDto.getDescription())
                .reportDate(LocalDateTime.now())
                .reporter(userService.getUser(username).orElseThrow(
                        () -> new IllegalArgumentException("User not found with username: " + username)
                ))
                .status(ReportStatus.PENDING);

        // docsType에 따라 관련 엔티티 설정
        switch (reportRequestDto.getDocsType()) {
            case COMMENT:
                Comment comment = commentService.getComment(UUID.fromString(reportRequestDto.getId()));
                reportBuilder.comment(comment);
                break;
            case NOTICE:
                Notice notice = experienceService.getNotice(UUID.fromString(reportRequestDto.getId()));
                reportBuilder.notice(notice);
                break;
            case POST:
                Post post = postService.getPost(Long.valueOf(reportRequestDto.getId()));
                reportBuilder.post(post);
                break;
            case REVIEW:
                Review review = reviewService.getReview(UUID.fromString(reportRequestDto.getId()));
                reportBuilder.review(review);
                break;

            default:
                throw new IllegalArgumentException("Unsupported docsType: " + reportRequestDto.getDocsType());
        }

        // Report 생성 및 저장
        Report report = reportBuilder.build();
        reportRepository.save(report);
    }

//    public List<ReportResponseDto> getReportList(String username) {
//        return reportRepository.findAllByReporterUsername(username).stream()
//                .map(ReportResponseDto::new)
//                .toList();
//    }

    public ReportResponseDto getReportDetail(String username, Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + id));
        User reporter = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );
        if(!reporter.getRole().checkAdmin()) {
            throw new SecurityException("You are not an admin.");
        }
        return new ReportResponseDto(report);
    }
}
