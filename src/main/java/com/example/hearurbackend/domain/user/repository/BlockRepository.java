package com.example.hearurbackend.domain.user.repository;

import com.example.hearurbackend.domain.user.entity.Block;
import com.example.hearurbackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByBlockerAndBlocked(User meUser, User youUser);
}
