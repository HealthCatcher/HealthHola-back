package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.survey.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
