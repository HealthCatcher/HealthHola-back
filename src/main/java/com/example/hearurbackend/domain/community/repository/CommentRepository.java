package com.example.hearurbackend.domain.community.repository;

import com.example.hearurbackend.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
