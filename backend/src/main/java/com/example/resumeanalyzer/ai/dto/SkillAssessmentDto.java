package com.example.resumeanalyzer.ai.dto;

public class SkillAssessmentDto {

    private String skillName;
    private String category;
    private String importance; // "REQUIRED" or "PREFERRED"
    private String assessmentStatus; // "MATCHED", "PARTIAL", "MISSING"
    private String evidence;
    private String explanation;

    public SkillAssessmentDto() {}

    public SkillAssessmentDto(String skillName, String category, String importance, String assessmentStatus, String evidence, String explanation) {
        this.skillName = skillName;
        this.category = category;
        this.importance = importance;
        this.assessmentStatus = assessmentStatus;
        this.evidence = evidence;
        this.explanation = explanation;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getAssessmentStatus() {
        return assessmentStatus;
    }

    public void setAssessmentStatus(String assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
