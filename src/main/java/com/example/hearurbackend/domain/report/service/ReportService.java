package com.example.hearurbackend.domain.report.service;

import com.example.hearurbackend.domain.community.service.CommentService;
import com.example.hearurbackend.domain.community.service.PostService;
import com.example.hearurbackend.domain.experience.service.NoticeService;
import com.example.hearurbackend.domain.experience.service.ReviewService;
import com.example.hearurbackend.domain.user.service.UserService;
import com.example.hearurbackend.domain.util.DocsType;
import com.example.hearurbackend.domain.report.type.ReportStatus;
import com.example.hearurbackend.domain.user.type.UserRole;
import com.example.hearurbackend.domain.report.dto.ReportProcessRequestDto;
import com.example.hearurbackend.domain.report.dto.ReportRequestDto;
import com.example.hearurbackend.domain.report.dto.ReportResponseDto;
import com.example.hearurbackend.domain.report.entity.Report;
import com.example.hearurbackend.domain.community.entity.Comment;
import com.example.hearurbackend.domain.community.entity.Post;
import com.example.hearurbackend.domain.experience.entity.Notice;
import com.example.hearurbackend.domain.experience.entity.Review;
import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .docsType(reportRequestDto.getDocsType())
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

            case ASK:
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
        User user = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );
        if(user.getRole().equals(UserRole.ROLE_ADMIN) || report.getReporter().getUsername().equals(username)) {
            return new ReportResponseDto(report);
        }
        else{
            throw new SecurityException("You are not an admin.");
        }
    }

    public List<ReportResponseDto> getReportList(String username){
        User reporter = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );
        if(!reporter.getRole().checkAdmin()) {
            throw new SecurityException("You are not an admin.");
        }
        return reportRepository.findAll().stream()
                .map(ReportResponseDto::new)
                .toList();
    }

    public void processReport(String username, Long id, ReportProcessRequestDto reportProcessRequestDto) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + id));
        User admin = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );
        if(!admin.getRole().checkAdmin()) {
            throw new SecurityException("You are not an admin.");
        }
        report.processReport(reportProcessRequestDto);
        reportRepository.save(report);
    }

    @Transactional
    public List<ReportResponseDto> getMyReportList(String username) {
        User reporter = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );
        return reportRepository.findAllByReporterUsername(username).stream()
                .map(ReportResponseDto::new)
                .toList();
    }

    @Transactional
    public List<ReportResponseDto> getMyReportListAsk(String username) {
        User reporter = userService.getUser(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );
        return reportRepository.findAllByReporterUsernameAndDocsType(username, DocsType.ASK).stream()
                .map(ReportResponseDto::new)
                .toList();
    }
}
