package com.example.hearurbackend.entity.user;

import com.example.hearurbackend.domain.UserRole;
import com.example.hearurbackend.entity.community.Comment;
import com.example.hearurbackend.entity.community.Like;
import com.example.hearurbackend.entity.diagnosis.MentalHealth;
import com.example.hearurbackend.entity.community.Post;
import com.example.hearurbackend.entity.diagnosis.HealthRecord;
import com.example.hearurbackend.entity.diagnosis.Lifestyle;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.experience.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    private String username;
    private String password; // normal user
    private String name;
    private String nickname;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Integer age;

    private String gender;
    private int point;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<HealthRecord> healthRecords = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Lifestyle lifestyle;

    @OneToOne(mappedBy = "user")
    private MentalHealth mentalHealth;

    @OneToMany(mappedBy = "author")
    private List<Notice> myExperiences = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @ManyToMany(mappedBy = "participants")
    private List<Notice> participatedExperiences = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorite_notices",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "noticeId")
    )
    private Set<Notice> favoriteNotices = new HashSet<>();

    public User(String username, String password, String name, String email, UserRole role, String nickname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.nickname = nickname;
        this.point = 0;
    }


    public void createOAuthUser(String username, String email, String name, UserRole role) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
        this.nickname = name;
        this.point = 0;
    }

    public void updateOAuthUser(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public void changePassword(String password) {
        this.password = password;
    }
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ROLE_ADMIN;
    }
}
