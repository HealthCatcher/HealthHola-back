package com.example.hearurbackend.repository;

import com.example.hearurbackend.entity.survey.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
}
