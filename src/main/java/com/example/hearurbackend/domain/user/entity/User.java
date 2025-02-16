package com.example.hearurbackend.domain.user.entity;

import com.example.hearurbackend.domain.report.entity.Report;
import com.example.hearurbackend.domain.survey.entity.UserResponse;
import com.example.hearurbackend.domain.user.type.UserRole;
import com.example.hearurbackend.domain.community.entity.Comment;
import com.example.hearurbackend.domain.community.entity.Like;
import com.example.hearurbackend.domain.community.entity.Post;
import com.example.hearurbackend.domain.experience.entity.Notice;
import com.example.hearurbackend.domain.experience.entity.ParticipantEntry;
import com.example.hearurbackend.domain.experience.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL)
    private Set<Block> blocks = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> myExperiences = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantEntry> participatedExperiences = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_favorite_notices",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "noticeId")
    )
    private Set<Notice> favoriteNotices = new HashSet<>();

    private LocalDateTime couponExpirationDate;
    private LocalDateTime createdAt;
    private LocalDateTime accountExpiredAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lastPasswordChangedAt;
    private LocalDateTime accountSuspensionDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;
    private Boolean isRegistered;

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserResponse> userResponses = new ArrayList<>();

    public User(String username, String password, String name, String email, UserRole role, String nickname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.nickname = nickname;
        this.point = 0;
        this.createdAt = LocalDateTime.now();
        this.accountExpiredAt = LocalDateTime.now().plusYears(3);
        this.isRegistered = true;
    }


    public void createOAuthUser(String username, String email, String name, UserRole role) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
        this.nickname = name;
        this.point = 0;
        this.createdAt = LocalDateTime.now();
        this.accountExpiredAt = LocalDateTime.now().plusYears(3);
        this.isRegistered = false;
    }

    public void registerOAuthUser(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
        this.isRegistered = true;
    }

    public void updateOAuthUser(String email, String name) {
        this.email = email;
        this.name = name;
        this.isRegistered = true;
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

    public void usePremiumCoupon() {
        this.role = UserRole.ROLE_PREMIUM;
        this.couponExpirationDate = LocalDateTime.now().plusMonths(3);
    }

    public void usePriorityCoupon() {
        this.role = UserRole.ROLE_PRIORITY;
        this.couponExpirationDate = LocalDateTime.now().plusMonths(3);
    }

    public boolean isCouponUsed() {
        return this.role == UserRole.ROLE_PREMIUM || this.role == UserRole.ROLE_PRIORITY;
    }

    public void suspendAccount(int days) {
        this.accountSuspensionDate = LocalDateTime.now().plusDays(days);
    }

    public void unsuspendAccount() {
        this.accountSuspensionDate = null;
    }
    public void addFavoriteNotice(Notice notice) {
        this.favoriteNotices.add(notice);
        notice.getFavoriteUsers().add(this);  // Notice 쪽의 리스트에도 User를 추가
    }

    public void removeFavoriteNotice(Notice notice) {
        this.favoriteNotices.remove(notice);
        notice.getFavoriteUsers().remove(this);  // Notice 쪽의 리스트에서 User를 제거
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    public void changeAddress(Address address) {
        this.address = address;
    }
    public void withdrawUser() {
        this.favoriteNotices.clear();
        this.blocks.clear();
    }

    public void blockUser(User youUser) {
        Block block = new Block(this, youUser);
        this.blocks.add(block);
    }

    public void unblockUser(User blockedUser) {
        this.blocks.removeIf(block -> block.getBlocked().equals(blockedUser));
    }
}
