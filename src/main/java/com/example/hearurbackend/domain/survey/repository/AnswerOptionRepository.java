package com.example.hearurbackend.domain.survey.repository;

import com.example.hearurbackend.domain.survey.entity.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
}
