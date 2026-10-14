package com.example.resumeanalyzer.analysis.dto;

import com.example.resumeanalyzer.analysis.entity.AnalysisStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class AnalysisResponse {

    private Long id;
    private Long resumeId;
    private String resumeFilename;
    private Long jobDescriptionId;
    private String jobTitle;
    private String company;
    private BigDecimal matchScore;
    private AnalysisStatus status;
    private Instant createdAt;

    public AnalysisResponse() {}

    public AnalysisResponse(Long id, Long resumeId, String resumeFilename, Long jobDescriptionId, String jobTitle, String company, BigDecimal matchScore, AnalysisStatus status, Instant createdAt) {
        this.id = id;
        this.resumeId = resumeId;
        this.resumeFilename = resumeFilename;
        this.jobDescriptionId = jobDescriptionId;
        this.jobTitle = jobTitle;
        this.company = company;
        this.matchScore = matchScore;
        this.status = status;
        this.createdAt = createdAt;
    }

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

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(BigDecimal matchScore) {
        this.matchScore = matchScore;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
