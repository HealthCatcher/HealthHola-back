package com.example.hearurbackend.domain.experience.entity;

import com.example.hearurbackend.domain.experience.dto.ReviewRequestDto;
import com.example.hearurbackend.domain.report.entity.Report;
import com.example.hearurbackend.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Entity
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "experience_notice_id")
    private Notice experienceNotice;

    private String content;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @ElementCollection
    private List<String> urls = new ArrayList<>();

    public Review(User user, Notice experienceNotice, ReviewRequestDto reviewRequestDto) {
        this.createdAt = LocalDateTime.now();
        this.user = user;
        this.experienceNotice = experienceNotice;
        this.content = reviewRequestDto.getContent();
        this.urls = reviewRequestDto.getUrls();
    }

    public void updateReview(String content, List<String> urls) {
        this.content = content;
        this.urls = urls;
    }

    @Transactional
    public boolean checkReviewReported() {
        return !this.reports.isEmpty();
    }
}
