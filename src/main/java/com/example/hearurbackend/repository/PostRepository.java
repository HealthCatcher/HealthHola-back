package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Boolean existsByAuthor(String username);
}
