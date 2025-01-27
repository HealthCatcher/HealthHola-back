package com.example.hearurbackend.domain.survey.repository;

import com.example.hearurbackend.domain.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
