package com.example.hearurbackend.domain.community.entity;

import com.example.hearurbackend.domain.report.entity.Report;
import com.example.hearurbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    private String category;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private boolean isUpdated;
    private int views;

    @ElementCollection
    private List<String> imageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comments;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @Builder
    public Post(String category, String title, String content, String author, LocalDateTime createDate, LocalDateTime updateDate, boolean isUpdated, User user) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.isUpdated = isUpdated;
        this.user = user;
    }

    public void updatePost(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
        this.updateDate = LocalDateTime.now();
        this.isUpdated = true;
    }

    public void increaseViews() {
        this.views++;
    }

    public int getLikesCount() {
        return likes.size();
    }

    public int getCommentsCount() {
        return comments.size();
    }

    public void addImageUrl(String imageUrl) {
        this.imageUrl.add(imageUrl);
    }
}
