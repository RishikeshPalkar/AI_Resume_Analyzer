package com.example.resumeanalyzer.analysis.dto;

import com.example.resumeanalyzer.ai.dto.EvidenceDto;
import com.example.resumeanalyzer.ai.dto.KeywordMatchDto;
import com.example.resumeanalyzer.ai.dto.SkillAssessmentDto;
import com.example.resumeanalyzer.analysis.entity.AnalysisStatus;
import com.example.resumeanalyzer.analysis.scoring.ScoreBreakdown;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AnalysisDetailResponse {

    private Long id;
    private Long resumeId;
    private String resumeFilename;
    private Long jobDescriptionId;
    private String jobTitle;
    private String company;
    private AnalysisStatus status;
    private BigDecimal matchScore;
    private ScoreBreakdown scoreBreakdown;

    private String summary;
    private List<String> matchingSkills = new ArrayList<>();
    private List<String> missingRequiredSkills = new ArrayList<>();
    private List<String> missingPreferredSkills = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private List<KeywordMatchDto> keywordAnalysis = new ArrayList<>();
    private List<EvidenceDto> evidence = new ArrayList<>();
    private List<SkillAssessmentDto> skillAssessments = new ArrayList<>();

    private String modelIdentifier;
    private String promptVersion;
    private Instant createdAt;
    private Instant completedAt;
    private String disclaimer = "The match score and analysis are automated engineering estimates based on keyword and semantic alignment. They do not constitute an employer ATS pass guarantee.";

    public AnalysisDetailResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getResumeFilename() {
        return resumeFilename;
    }

    public void setResumeFilename(String resumeFilename) {
        this.resumeFilename = resumeFilename;
    }

    public Long getJobDescriptionId() {
        return jobDescriptionId;
    }

    public void setJobDescriptionId(Long jobDescriptionId) {
        this.jobDescriptionId = jobDescriptionId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(BigDecimal matchScore) {
        this.matchScore = matchScore;
    }

    public ScoreBreakdown getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(ScoreBreakdown scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills != null ? matchingSkills : new ArrayList<>();
    }

    public List<String> getMissingRequiredSkills() {
        return missingRequiredSkills;
    }

    public void setMissingRequiredSkills(List<String> missingRequiredSkills) {
        this.missingRequiredSkills = missingRequiredSkills != null ? missingRequiredSkills : new ArrayList<>();
    }

    public List<String> getMissingPreferredSkills() {
        return missingPreferredSkills;
    }

    public void setMissingPreferredSkills(List<String> missingPreferredSkills) {
        this.missingPreferredSkills = missingPreferredSkills != null ? missingPreferredSkills : new ArrayList<>();
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths != null ? strengths : new ArrayList<>();
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    public List<KeywordMatchDto> getKeywordAnalysis() {
        return keywordAnalysis;
    }

    public void setKeywordAnalysis(List<KeywordMatchDto> keywordAnalysis) {
        this.keywordAnalysis = keywordAnalysis != null ? keywordAnalysis : new ArrayList<>();
    }

    public List<EvidenceDto> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<EvidenceDto> evidence) {
        this.evidence = evidence != null ? evidence : new ArrayList<>();
    }

    public List<SkillAssessmentDto> getSkillAssessments() {
        return skillAssessments;
    }

    public void setSkillAssessments(List<SkillAssessmentDto> skillAssessments) {
        this.skillAssessments = skillAssessments != null ? skillAssessments : new ArrayList<>();
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
