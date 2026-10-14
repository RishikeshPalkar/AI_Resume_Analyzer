package com.example.resumeanalyzer.analysis.scoring;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ScoreBreakdown {

    private BigDecimal overallScore;
    private BigDecimal requiredSkillsScore;
    private BigDecimal experienceEvidenceScore;
    private BigDecimal qualificationsScore;
    private BigDecimal preferredSkillsScore;

    private int requiredSkillsTotal;
    private int requiredSkillsMatched;
    private int preferredSkillsTotal;
    private int preferredSkillsMatched;

    private String formulaExplanation;

    public ScoreBreakdown() {}

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(BigDecimal overallScore) {
        this.overallScore = overallScore != null ? overallScore.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getRequiredSkillsScore() {
        return requiredSkillsScore;
    }

    public void setRequiredSkillsScore(BigDecimal requiredSkillsScore) {
        this.requiredSkillsScore = requiredSkillsScore;
    }

    public BigDecimal getExperienceEvidenceScore() {
        return experienceEvidenceScore;
    }

    public void setExperienceEvidenceScore(BigDecimal experienceEvidenceScore) {
        this.experienceEvidenceScore = experienceEvidenceScore;
    }

    public BigDecimal getQualificationsScore() {
        return qualificationsScore;
    }

    public void setQualificationsScore(BigDecimal qualificationsScore) {
        this.qualificationsScore = qualificationsScore;
    }

    public BigDecimal getPreferredSkillsScore() {
        return preferredSkillsScore;
    }

    public void setPreferredSkillsScore(BigDecimal preferredSkillsScore) {
        this.preferredSkillsScore = preferredSkillsScore;
    }

    public int getRequiredSkillsTotal() {
        return requiredSkillsTotal;
    }

    public void setRequiredSkillsTotal(int requiredSkillsTotal) {
        this.requiredSkillsTotal = requiredSkillsTotal;
    }

    public int getRequiredSkillsMatched() {
        return requiredSkillsMatched;
    }

    public void setRequiredSkillsMatched(int requiredSkillsMatched) {
        this.requiredSkillsMatched = requiredSkillsMatched;
    }

    public int getPreferredSkillsTotal() {
        return preferredSkillsTotal;
    }

    public void setPreferredSkillsTotal(int preferredSkillsTotal) {
        this.preferredSkillsTotal = preferredSkillsTotal;
    }

    public int getPreferredSkillsMatched() {
        return preferredSkillsMatched;
    }

    public void setPreferredSkillsMatched(int preferredSkillsMatched) {
        this.preferredSkillsMatched = preferredSkillsMatched;
    }

    public String getFormulaExplanation() {
        return formulaExplanation;
    }

    public void setFormulaExplanation(String formulaExplanation) {
        this.formulaExplanation = formulaExplanation;
    }
}
