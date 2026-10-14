package com.example.resumeanalyzer.jobdescription.dto;

import java.time.Instant;

public class JobDescriptionResponse {

    private Long id;
    private String title;
    private String company;
    private String descriptionText;
    private Instant createdAt;

    public JobDescriptionResponse() {}

    public JobDescriptionResponse(Long id, String title, String company, String descriptionText, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.descriptionText = descriptionText;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
