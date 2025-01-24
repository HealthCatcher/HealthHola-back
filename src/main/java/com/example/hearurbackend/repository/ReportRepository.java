package com.example.hearurbackend.repository;

import com.example.hearurbackend.domain.DocsType;
import com.example.hearurbackend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByReporterUsername(String username);

    List<Report> findAllByReporterUsernameAndDocsType(String username, DocsType docsType);
}
