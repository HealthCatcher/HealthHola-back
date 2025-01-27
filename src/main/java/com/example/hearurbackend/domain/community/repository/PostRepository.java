package com.example.hearurbackend.domain.community.repository;

import com.example.hearurbackend.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Boolean existsByAuthor(String username);
}
