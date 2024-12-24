package com.example.hearurbackend.entity.experience;

import com.example.hearurbackend.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Entity
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

    public Review(User user, Notice experienceNotice, String content) {
        this.createdAt = LocalDateTime.now();
        this.user = user;
        this.experienceNotice = experienceNotice;
        this.content = content;
    }

    public void updateReview(String content) {
        this.content = content;
    }
}
