package com.example.resumeanalyzer.analysis.repository;

import com.example.resumeanalyzer.analysis.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    Optional<AnalysisResult> findByAnalysisId(Long analysisId);
}
