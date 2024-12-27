package com.example.hearurbackend.entity.experience;

import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class ParticipantEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Notice notice;

    @ManyToOne
    private User user;

    private int entryCount;
    private LocalDateTime lastEntryDate;

    public ParticipantEntry(Notice notice, User user){
        this.notice = notice;
        this.user = user;
        this.entryCount = 1;
        this.lastEntryDate = LocalDateTime.now();
    }
}
