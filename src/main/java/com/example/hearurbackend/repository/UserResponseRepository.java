package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.survey.Question;
import com.example.hearurbackend.entity.survey.UserResponse;
import com.example.hearurbackend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
    boolean existsByUserAndQuestion(User user, Question question);
}
