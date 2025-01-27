package com.example.hearurbackend.domain.experience.repository;

import com.example.hearurbackend.domain.experience.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExperienceNoticeRepository extends JpaRepository<Notice, UUID> {
}
