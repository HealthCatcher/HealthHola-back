package com.example.hearurbackend.domain.survey.repository;

import com.example.hearurbackend.domain.survey.entity.Question;
import com.example.hearurbackend.domain.survey.entity.UserResponse;
import com.example.hearurbackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
    boolean existsByUserAndQuestion(User user, Question question);
}
