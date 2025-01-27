package com.example.hearurbackend.domain.experience.repository;

import com.example.hearurbackend.domain.experience.entity.Notice;
import com.example.hearurbackend.domain.experience.entity.ParticipantEntry;
import com.example.hearurbackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantEntryRepository extends JpaRepository<ParticipantEntry, Long> {
    Optional<ParticipantEntry> findByNoticeAndUser(Notice notice, User user);
}
