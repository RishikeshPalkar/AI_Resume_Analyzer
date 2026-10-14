package com.example.resumeanalyzer.scoring;

import com.example.resumeanalyzer.ai.dto.AiAnalysisResultDto;
import com.example.resumeanalyzer.ai.dto.SkillAssessmentDto;
import com.example.resumeanalyzer.analysis.scoring.ScoreBreakdown;
import com.example.resumeanalyzer.analysis.scoring.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoringServiceTest {

    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        // Default weights: 50% required skills, 25% experience, 15% qualifications, 10% preferred skills
        scoringService = new ScoringService(50.0, 25.0, 15.0, 10.0);
    }

    @Test
    @DisplayName("Should return 100% when all requirements, experience, and qualifications match perfectly")
    void testPerfectMatch() {
        AiAnalysisResultDto result = new AiAnalysisResultDto();
        result.setExperienceAlignmentRatio(1.0);
        result.setQualificationsAlignmentRatio(1.0);

        List<SkillAssessmentDto> skills = List.of(
                new SkillAssessmentDto("Java", "Language", "REQUIRED", "MATCHED", "5 years experience", "Exact match"),
                new SkillAssessmentDto("Spring Boot", "Framework", "REQUIRED", "MATCHED", "Hands-on projects", "Exact match"),
                new SkillAssessmentDto("Docker", "DevOps", "PREFERRED", "MATCHED", "Containerization used", "Exact match")
        );
        result.setSkillAssessments(skills);

        ScoreBreakdown breakdown = scoringService.calculateScore(result);

        assertNotNull(breakdown);
        assertEquals(new BigDecimal("100.00"), breakdown.getOverallScore());
        assertEquals(new BigDecimal("50.00"), breakdown.getRequiredSkillsScore());
        assertEquals(new BigDecimal("25.00"), breakdown.getExperienceEvidenceScore());
        assertEquals(new BigDecimal("15.00"), breakdown.getQualificationsScore());
        assertEquals(new BigDecimal("10.00"), breakdown.getPreferredSkillsScore());
    }

    @Test
    @DisplayName("Should correctly compute partial matches and missing skills")
    void testPartialAndMissingSkills() {
        AiAnalysisResultDto result = new AiAnalysisResultDto();
        result.setExperienceAlignmentRatio(0.5); // 0.5 * 25 = 12.50
        result.setQualificationsAlignmentRatio(0.6); // 0.6 * 15 = 9.00

        List<SkillAssessmentDto> skills = List.of(
                // Required: 1 matched (1.0), 1 partial (0.5), 1 missing (0.0) -> Total 1.5 / 3 = 0.5 -> 0.5 * 50 = 25.00
                new SkillAssessmentDto("Java", "Language", "REQUIRED", "MATCHED", "Found", "Good"),
                new SkillAssessmentDto("SQL", "Database", "REQUIRED", "PARTIAL", "Basic query knowledge", "Partial"),
                new SkillAssessmentDto("Kubernetes", "DevOps", "REQUIRED", "MISSING", "None", "Missing"),
                // Preferred: 1 missing (0.0) -> 0.0 * 10 = 0.00
                new SkillAssessmentDto("AWS", "Cloud", "PREFERRED", "MISSING", "None", "Missing")
        );
        result.setSkillAssessments(skills);

        ScoreBreakdown breakdown = scoringService.calculateScore(result);

        assertNotNull(breakdown);
        // Total = 25.00 + 12.50 + 9.00 + 0.00 = 46.50
        assertEquals(new BigDecimal("46.50"), breakdown.getOverallScore());
        assertEquals(3, breakdown.getRequiredSkillsTotal());
        assertEquals(1, breakdown.getRequiredSkillsMatched());
        assertEquals(1, breakdown.getPreferredSkillsTotal());
        assertEquals(0, breakdown.getPreferredSkillsMatched());
    }

    @Test
    @DisplayName("Should prevent double counting duplicate skill mentions")
    void testPreventDoubleCounting() {
        AiAnalysisResultDto result = new AiAnalysisResultDto();
        result.setExperienceAlignmentRatio(1.0);
        result.setQualificationsAlignmentRatio(1.0);

        List<SkillAssessmentDto> skills = List.of(
                new SkillAssessmentDto("Java", "Language", "REQUIRED", "MATCHED", "Exp 1", "Match"),
                new SkillAssessmentDto("java", "Language", "REQUIRED", "MATCHED", "Exp 2", "Duplicate"),
                new SkillAssessmentDto("JAVA", "Language", "REQUIRED", "MATCHED", "Exp 3", "Duplicate")
        );
        result.setSkillAssessments(skills);

        ScoreBreakdown breakdown = scoringService.calculateScore(result);

        assertEquals(1, breakdown.getRequiredSkillsTotal());
        assertEquals(1, breakdown.getRequiredSkillsMatched());
    }

    @Test
    @DisplayName("Should handle empty result safely without throwing exceptions")
    void testNullOrEmptyInput() {
        ScoreBreakdown breakdown = scoringService.calculateScore(null);
        assertNotNull(breakdown);
        assertEquals(new BigDecimal("0.00"), breakdown.getOverallScore());
    }
}
