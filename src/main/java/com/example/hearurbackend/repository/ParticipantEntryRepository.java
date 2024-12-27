package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.experience.ParticipantEntry;
import com.example.hearurbackend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantEntryRepository extends JpaRepository<ParticipantEntry, Long> {
    Optional<ParticipantEntry> findByNoticeAndUser(Notice notice, User user);
}
