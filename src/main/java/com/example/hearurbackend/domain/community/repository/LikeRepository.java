package com.example.hearurbackend.domain.community.repository;

import com.example.hearurbackend.domain.community.entity.Like;
import com.example.hearurbackend.domain.community.entity.Post;
import com.example.hearurbackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);
}
