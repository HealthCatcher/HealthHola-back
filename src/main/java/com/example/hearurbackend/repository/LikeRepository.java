package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.community.Like;
import com.example.hearurbackend.entity.community.Post;
import com.example.hearurbackend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);
}
