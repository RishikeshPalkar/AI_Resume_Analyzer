package com.example.resumeanalyzer.analysis.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "analysis_results")
@EntityListeners(AuditingEntityListener.class)
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false, unique = true)
    private Analysis analysis;

    @Lob
    @Column(name = "matching_skills_json", columnDefinition = "LONGTEXT")
    private String matchingSkillsJson;

    @Lob
    @Column(name = "missing_required_skills_json", columnDefinition = "LONGTEXT")
    private String missingRequiredSkillsJson;

    @Lob
    @Column(name = "missing_preferred_skills_json", columnDefinition = "LONGTEXT")
    private String missingPreferredSkillsJson;

    @Lob
    @Column(name = "strengths_json", columnDefinition = "LONGTEXT")
    private String strengthsJson;

    @Lob
    @Column(name = "recommendations_json", columnDefinition = "LONGTEXT")
    private String recommendationsJson;

    @Lob
    @Column(name = "keyword_analysis_json", columnDefinition = "LONGTEXT")
    private String keywordAnalysisJson;

    @Lob
    @Column(name = "evidence_json", columnDefinition = "LONGTEXT")
    private String evidenceJson;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AnalysisResult() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public String getMatchingSkillsJson() {
        return matchingSkillsJson;
    }

    public void setMatchingSkillsJson(String matchingSkillsJson) {
        this.matchingSkillsJson = matchingSkillsJson;
    }

    public String getMissingRequiredSkillsJson() {
        return missingRequiredSkillsJson;
    }

    public void setMissingRequiredSkillsJson(String missingRequiredSkillsJson) {
        this.missingRequiredSkillsJson = missingRequiredSkillsJson;
    }

    public String getMissingPreferredSkillsJson() {
        return missingPreferredSkillsJson;
    }

    public void setMissingPreferredSkillsJson(String missingPreferredSkillsJson) {
        this.missingPreferredSkillsJson = missingPreferredSkillsJson;
    }

    public String getStrengthsJson() {
        return strengthsJson;
    }

    public void setStrengthsJson(String strengthsJson) {
        this.strengthsJson = strengthsJson;
    }

    public String getRecommendationsJson() {
        return recommendationsJson;
    }

    public void setRecommendationsJson(String recommendationsJson) {
        this.recommendationsJson = recommendationsJson;
    }

    public String getKeywordAnalysisJson() {
        return keywordAnalysisJson;
    }

    public void setKeywordAnalysisJson(String keywordAnalysisJson) {
        this.keywordAnalysisJson = keywordAnalysisJson;
    }

    public String getEvidenceJson() {
        return evidenceJson;
    }

    public void setEvidenceJson(String evidenceJson) {
        this.evidenceJson = evidenceJson;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
