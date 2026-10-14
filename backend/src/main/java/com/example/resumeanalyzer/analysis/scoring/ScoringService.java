package com.example.resumeanalyzer.analysis.scoring;

import com.example.resumeanalyzer.ai.dto.AiAnalysisResultDto;
import com.example.resumeanalyzer.ai.dto.SkillAssessmentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ScoringService {

    private final double requiredSkillWeight;
    private final double experienceEvidenceWeight;
    private final double qualificationsWeight;
    private final double preferredSkillWeight;

    public ScoringService(
            @Value("${app.scoring.required-skill-weight:50}") double requiredSkillWeight,
            @Value("${app.scoring.experience-evidence-weight:25}") double experienceEvidenceWeight,
            @Value("${app.scoring.qualifications-weight:15}") double qualificationsWeight,
            @Value("${app.scoring.preferred-skill-weight:10}") double preferredSkillWeight) {
        this.requiredSkillWeight = requiredSkillWeight;
        this.experienceEvidenceWeight = experienceEvidenceWeight;
        this.qualificationsWeight = qualificationsWeight;
        this.preferredSkillWeight = preferredSkillWeight;
    }

    public ScoreBreakdown calculateScore(AiAnalysisResultDto aiResult) {
        ScoreBreakdown breakdown = new ScoreBreakdown();

        if (aiResult == null) {
            breakdown.setOverallScore(BigDecimal.ZERO);
            breakdown.setFormulaExplanation("No analysis input provided");
            return breakdown;
        }

        List<SkillAssessmentDto> assessments = aiResult.getSkillAssessments();
        Set<String> processedSkills = new HashSet<>();

        double requiredPoints = 0.0;
        int requiredCount = 0;
        int requiredMatchedCount = 0;

        double preferredPoints = 0.0;
        int preferredCount = 0;
        int preferredMatchedCount = 0;

        for (SkillAssessmentDto s : assessments) {
            if (s.getSkillName() == null || s.getSkillName().isBlank()) continue;
            String normalizedSkill = s.getSkillName().trim().toLowerCase();
            if (processedSkills.contains(normalizedSkill)) continue; // prevent double counting
            processedSkills.add(normalizedSkill);

            boolean isRequired = s.getImportance() == null || !"PREFERRED".equalsIgnoreCase(s.getImportance());
            String status = s.getAssessmentStatus() != null ? s.getAssessmentStatus().toUpperCase() : "MISSING";

            double statusWeight = switch (status) {
                case "MATCHED" -> 1.0;
                case "PARTIAL" -> 0.5;
                default -> 0.0;
            };

            if (isRequired) {
                requiredCount++;
                requiredPoints += statusWeight;
                if ("MATCHED".equals(status)) requiredMatchedCount++;
            } else {
                preferredCount++;
                preferredPoints += statusWeight;
                if ("MATCHED".equals(status)) preferredMatchedCount++;
            }
        }

        // Calculate alignment ratios (0.0 to 1.0)
        double requiredRatio = requiredCount > 0 ? (requiredPoints / requiredCount) : 1.0;
        double preferredRatio = preferredCount > 0 ? (preferredPoints / preferredCount) : 1.0;
        double experienceRatio = aiResult.getExperienceAlignmentRatio();
        double qualificationsRatio = aiResult.getQualificationsAlignmentRatio();

        // Calculate component scores
        BigDecimal reqScore = BigDecimal.valueOf(requiredRatio * requiredSkillWeight).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expScore = BigDecimal.valueOf(experienceRatio * experienceEvidenceWeight).setScale(2, RoundingMode.HALF_UP);
        BigDecimal qualScore = BigDecimal.valueOf(qualificationsRatio * qualificationsWeight).setScale(2, RoundingMode.HALF_UP);
        BigDecimal prefScore = BigDecimal.valueOf(preferredRatio * preferredSkillWeight).setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = reqScore.add(expScore).add(qualScore).add(prefScore);
        // Bound between 0 and 100
        total = total.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100.00));

        breakdown.setOverallScore(total);
        breakdown.setRequiredSkillsScore(reqScore);
        breakdown.setExperienceEvidenceScore(expScore);
        breakdown.setQualificationsScore(qualScore);
        breakdown.setPreferredSkillsScore(prefScore);

        breakdown.setRequiredSkillsTotal(requiredCount);
        breakdown.setRequiredSkillsMatched(requiredMatchedCount);
        breakdown.setPreferredSkillsTotal(preferredCount);
        breakdown.setPreferredSkillsMatched(preferredMatchedCount);

        breakdown.setFormulaExplanation(String.format(
                "Deterministic Match Score = Required Skills (%.1f%% of %.0f pts) + Experience (%.1f%% of %.0f pts) + Qualifications (%.1f%% of %.0f pts) + Preferred Skills (%.1f%% of %.0f pts) = %.2f%%",
                requiredRatio * 100, requiredSkillWeight,
                experienceRatio * 100, experienceEvidenceWeight,
                qualificationsRatio * 100, qualificationsWeight,
                preferredRatio * 100, preferredSkillWeight,
                total.doubleValue()
        ));

        return breakdown;
    }
}
