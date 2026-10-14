package com.example.resumeanalyzer.resume.dto;

import java.time.Instant;

public class ResumeDetailResponse {

    private Long id;
    private String originalFilename;
    private String detectedContentType;
    private String extractedText;
    private int characterCount;
    private Instant createdAt;

    public ResumeDetailResponse() {}

    public ResumeDetailResponse(Long id, String originalFilename, String detectedContentType, String extractedText, Instant createdAt) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.detectedContentType = detectedContentType;
        this.extractedText = extractedText;
        this.characterCount = extractedText != null ? extractedText.length() : 0;
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

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
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
