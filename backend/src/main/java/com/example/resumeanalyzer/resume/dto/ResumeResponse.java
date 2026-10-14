package com.example.resumeanalyzer.resume.dto;

import java.time.Instant;

public class ResumeResponse {

    private Long id;
    private String originalFilename;
    private String detectedContentType;
    private int characterCount;
    private Instant createdAt;

    public ResumeResponse() {}

    public ResumeResponse(Long id, String originalFilename, String detectedContentType, int characterCount, Instant createdAt) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.detectedContentType = detectedContentType;
        this.characterCount = characterCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getDetectedContentType() {
        return detectedContentType;
    }

    public void setDetectedContentType(String detectedContentType) {
        this.detectedContentType = detectedContentType;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(int characterCount) {
        this.characterCount = characterCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
