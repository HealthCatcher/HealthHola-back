package com.example.hearurbackend.domain.survey.repository;

import com.example.hearurbackend.domain.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long>{
}
