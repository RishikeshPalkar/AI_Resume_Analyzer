package com.example.resumeanalyzer.jobdescription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobDescriptionRequest {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Company cannot exceed 255 characters")
    private String company;

    @NotBlank(message = "Job description text is required")
    @Size(min = 50, max = 30000, message = "Job description text must be between 50 and 30,000 characters")
    private String descriptionText;

    public JobDescriptionRequest() {}

    public JobDescriptionRequest(String title, String company, String descriptionText) {
        this.title = title;
        this.company = company;
        this.descriptionText = descriptionText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }
}
