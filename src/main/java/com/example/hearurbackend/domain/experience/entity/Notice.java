package com.example.hearurbackend.domain.experience.entity;

import com.example.hearurbackend.domain.experience.dto.NoticeRequestDto;
import com.example.hearurbackend.domain.report.entity.Report;
import com.example.hearurbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Entity
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "author")
    private User author;

    private String category;
    private String title;
    private String location;
    private LocalDateTime createDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String company;
    private String content;
    private int price;
    private String campaignDetails;
    private String instruction;
    private int views;
    private int maxParticipants;

    @ElementCollection
    private List<String> titleImageUrl;

    @ElementCollection
    private List<String> detailImageUrls;

    @OneToMany(mappedBy = "experienceNotice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantEntry> participantEntries = new ArrayList<>();

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @ManyToMany(mappedBy = "favoriteNotices", cascade = CascadeType.ALL)
    private Set<User> favoriteUsers = new HashSet<>();



    @Builder
    public Notice(User author, String category, String title, String location, LocalDateTime createDate, LocalDateTime startDate, LocalDateTime endDate, String company, String content, int price, String campaignDetails, String instruction, int maxParticipants) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.location = location;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.company = company;
        this.content = content;
        this.price = price;
        this.campaignDetails = campaignDetails;
        this.instruction = instruction;
        this.views = 0;
        this.maxParticipants = maxParticipants;
    }

    public Notice(NoticeRequestDto dto, User author, LocalDateTime createDate) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.author = author;
        this.createDate = createDate;
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.category = dto.getCategory();
        this.campaignDetails = dto.getCampaignDetails();
        this.instruction = dto.getInstruction();
        this.maxParticipants = dto.getMaxParticipants();
    }

    public void updateNotice(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
    }

    public void increaseViews() {
        this.views++;
    }

    public int getFavoritesCount() {
        return favoriteUsers.size();
    }

    public void addFavoriteUser(User user) {
        this.favoriteUsers.add(user);
        user.getFavoriteNotices().add(this);
    }

    public void addTitleImageUrl(String fileUrl) {
        this.titleImageUrl.add(fileUrl);
    }

    public void addDetailImageUrl(String fileUrl) {
        this.detailImageUrls.add(fileUrl);
    }
}
