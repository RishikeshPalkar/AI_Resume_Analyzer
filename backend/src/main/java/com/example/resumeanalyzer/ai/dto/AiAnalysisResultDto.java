package com.example.resumeanalyzer.ai.dto;

import java.util.ArrayList;
import java.util.List;

public class AiAnalysisResultDto {

    private String summary;
    private List<String> matchingSkills = new ArrayList<>();
    private List<String> missingRequiredSkills = new ArrayList<>();
    private List<String> missingPreferredSkills = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private List<KeywordMatchDto> keywordAnalysis = new ArrayList<>();
    private List<EvidenceDto> evidence = new ArrayList<>();
    private List<SkillAssessmentDto> skillAssessments = new ArrayList<>();
    private String experienceEvidenceNotes;
    private String qualificationsNotes;
    private double experienceAlignmentRatio = 0.8;
    private double qualificationsAlignmentRatio = 0.8;

    public AiAnalysisResultDto() {}

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

    public String getExperienceEvidenceNotes() {
        return experienceEvidenceNotes;
    }

    public void setExperienceEvidenceNotes(String experienceEvidenceNotes) {
        this.experienceEvidenceNotes = experienceEvidenceNotes;
    }

    public String getQualificationsNotes() {
        return qualificationsNotes;
    }

    public void setQualificationsNotes(String qualificationsNotes) {
        this.qualificationsNotes = qualificationsNotes;
    }

    public double getExperienceAlignmentRatio() {
        return experienceAlignmentRatio;
    }

    public void setExperienceAlignmentRatio(double experienceAlignmentRatio) {
        this.experienceAlignmentRatio = Math.max(0.0, Math.min(1.0, experienceAlignmentRatio));
    }

    public double getQualificationsAlignmentRatio() {
        return qualificationsAlignmentRatio;
    }

    public void setQualificationsAlignmentRatio(double qualificationsAlignmentRatio) {
        this.qualificationsAlignmentRatio = Math.max(0.0, Math.min(1.0, qualificationsAlignmentRatio));
    }
}
