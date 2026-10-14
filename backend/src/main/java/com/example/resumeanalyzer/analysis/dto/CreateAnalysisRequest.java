package com.example.resumeanalyzer.analysis.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateAnalysisRequest {

    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    private Long jobDescriptionId;

    @Size(max = 255, message = "Job title cannot exceed 255 characters")
    private String jobTitle;

    @Size(max = 255, message = "Company cannot exceed 255 characters")
    private String company;

    @Size(min = 30, max = 30000, message = "Job description text must be between 30 and 30,000 characters")
    private String jobDescriptionText;

    public CreateAnalysisRequest() {}

    public CreateAnalysisRequest(Long resumeId, Long jobDescriptionId, String jobTitle, String company, String jobDescriptionText) {
        this.resumeId = resumeId;
        this.jobDescriptionId = jobDescriptionId;
        this.jobTitle = jobTitle;
        this.company = company;
        this.jobDescriptionText = jobDescriptionText;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
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

    public String getJobDescriptionText() {
        return jobDescriptionText;
    }

    public void setJobDescriptionText(String jobDescriptionText) {
        this.jobDescriptionText = jobDescriptionText;
    }
}
