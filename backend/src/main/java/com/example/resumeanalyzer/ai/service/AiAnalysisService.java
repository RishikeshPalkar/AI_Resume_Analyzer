package com.example.resumeanalyzer.ai.service;

import com.example.resumeanalyzer.ai.dto.AiAnalysisResultDto;
import com.example.resumeanalyzer.ai.dto.EvidenceDto;
import com.example.resumeanalyzer.ai.dto.KeywordMatchDto;
import com.example.resumeanalyzer.ai.dto.SkillAssessmentDto;
import com.example.resumeanalyzer.ai.prompt.AnalysisPromptTemplates;
import com.example.resumeanalyzer.common.exception.AiServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisService.class);

    private final Optional<ChatModel> chatModel;
    private final ObjectMapper objectMapper;
    private final boolean mockFallbackEnabled;
    private final String apiKey;

    public AiAnalysisService(
            @Autowired(required = false) Optional<ChatModel> chatModel,
            ObjectMapper objectMapper,
            @Value("${app.ai.mock-fallback-enabled:true}") boolean mockFallbackEnabled,
            @Value("${spring.ai.openai.api-key:demo-key}") String apiKey) {
        this.chatModel = chatModel != null ? chatModel : Optional.empty();
        this.objectMapper = objectMapper;
        this.mockFallbackEnabled = mockFallbackEnabled;
        this.apiKey = apiKey;
    }

    public AiAnalysisResultDto analyze(String resumeText, String jobDescriptionText) {
        // If a real OpenAI API key is supplied and chatModel is present, attempt LLM call
        if (hasValidApiKey() && chatModel.isPresent()) {
            try {
                return callSpringAi(resumeText, jobDescriptionText);
            } catch (Exception e) {
                log.warn("Spring AI call failed, falling back if enabled. Error: {}", e.getMessage());
                if (!mockFallbackEnabled) {
                    throw new AiServiceException("Failed to analyze resume with AI provider: " + e.getMessage(), e);
                }
            }
        }

        if (mockFallbackEnabled) {
            log.info("Using deterministic local analysis engine (no active external LLM key provided)");
            return generateDeterministicAnalysis(resumeText, jobDescriptionText);
        }

        throw new AiServiceException("AI provider is not configured and fallback mode is disabled.");
    }

    private boolean hasValidApiKey() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.contains("demo-key") && !apiKey.contains("test-mock-key");
    }

    private AiAnalysisResultDto callSpringAi(String resumeText, String jobDescriptionText) {
        String userContent = String.format(AnalysisPromptTemplates.USER_PROMPT_TEMPLATE, resumeText, jobDescriptionText);
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(AnalysisPromptTemplates.SYSTEM_PROMPT),
                new UserMessage(userContent)
        ));

        String rawContent = chatModel.get().call(prompt).getResult().getOutput().getText();
        return parseAndValidateAiResponse(rawContent);
    }

    private AiAnalysisResultDto parseAndValidateAiResponse(String rawJson) {
        try {
            // Strip markdown code fences if present (e.g. ```json ... ```)
            String cleaned = rawJson.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            AiAnalysisResultDto result = objectMapper.readValue(cleaned, AiAnalysisResultDto.class);
            validateAiResult(result);
            return result;
        } catch (Exception e) {
            log.error("Failed to parse AI response into structured DTO: {}", e.getMessage());
            throw new AiServiceException("Received invalid structured response from AI model", e);
        }
    }

    private void validateAiResult(AiAnalysisResultDto result) {
        if (result == null) {
            throw new AiServiceException("AI result was empty");
        }
        if (result.getSummary() == null || result.getSummary().isBlank()) {
            result.setSummary("Candidate profile evaluation completed against job requirements.");
        }
    }

    /**
     * Deterministic NLP keyword analyzer for development, testing, and fallback.
     * Identifies technical keywords, frameworks, databases, and architectural concepts.
     */
    public AiAnalysisResultDto generateDeterministicAnalysis(String resumeText, String jobText) {
        AiAnalysisResultDto result = new AiAnalysisResultDto();
        String resumeLower = resumeText.toLowerCase();
        String jobLower = jobText.toLowerCase();

        // Technical skill vocabulary
        Map<String, String> technicalSkills = getTechnicalSkillsCatalog();

        List<String> matching = new ArrayList<>();
        List<String> missingReq = new ArrayList<>();
        List<String> missingPref = new ArrayList<>();
        List<SkillAssessmentDto> assessments = new ArrayList<>();
        List<KeywordMatchDto> keywordMatches = new ArrayList<>();
        List<EvidenceDto> evidences = new ArrayList<>();

        for (Map.Entry<String, String> entry : technicalSkills.entrySet()) {
            String skill = entry.getKey();
            String category = entry.getValue();
            boolean inJob = containsWord(jobLower, skill.toLowerCase());
            boolean inResume = containsWord(resumeLower, skill.toLowerCase());

            if (inJob) {
                // Classify importance: keywords near "preferred", "plus", "nice to have" are preferred
                boolean isPreferred = jobLower.contains("preferred") && jobLower.indexOf("preferred") < jobLower.indexOf(skill.toLowerCase());
                String importance = isPreferred ? "PREFERRED" : "REQUIRED";

                if (inResume) {
                    matching.add(skill);
                    assessments.add(new SkillAssessmentDto(
                            skill, category, importance, "MATCHED",
                            "Directly referenced in resume experience or skills section",
                            "Strong match with job requirements."
                    ));
                    keywordMatches.add(new KeywordMatchDto(skill, true, "Demonstrated competency found in resume."));
                    evidences.add(new EvidenceDto(skill, "Mentioned in resume profile", "SUPPORTED"));
                } else {
                    if (isPreferred) {
                        missingPref.add(skill);
                    } else {
                        missingReq.add(skill);
                    }
                    assessments.add(new SkillAssessmentDto(
                            skill, category, importance, "MISSING",
                            "Not explicitly referenced in candidate resume",
                            "Identified in job requirements but absent from resume."
                    ));
                    keywordMatches.add(new KeywordMatchDto(skill, false, "Missing from resume profile."));
                    evidences.add(new EvidenceDto(skill, "Not found in resume", "NOT_FOUND"));
                }
            } else if (inResume && matching.size() < 8) {
                // Relevant skills present in resume that add general technical value
                matching.add(skill);
            }
        }

        // If no catalog skills matched the job description, extract capitalised potential requirements
        if (assessments.isEmpty()) {
            assessments.add(new SkillAssessmentDto("Software Engineering", "General", "REQUIRED",
                    containsWord(resumeLower, "software") ? "MATCHED" : "PARTIAL",
                    "Assessed against general engineering background", "General core alignment"));
        }

        result.setMatchingSkills(matching);
        result.setMissingRequiredSkills(missingReq);
        result.setMissingPreferredSkills(missingPref);
        result.setSkillAssessments(assessments);
        result.setKeywordAnalysis(keywordMatches);
        result.setEvidence(evidences);

        // Strengths & Recommendations
        List<String> strengths = new ArrayList<>();
        if (!matching.isEmpty()) {
            strengths.add("Demonstrated core proficiency in " + String.join(", ", matching.stream().limit(4).toList()));
        }
        if (resumeLower.contains("lead") || resumeLower.contains("architect") || resumeLower.contains("senior")) {
            strengths.add("Demonstrated senior engineering and leadership background.");
        }
        if (resumeLower.contains("microservices") || resumeLower.contains("distributed") || resumeLower.contains("rest api")) {
            strengths.add("Solid hands-on background in scalable distributed systems and API design.");
        }
        if (strengths.isEmpty()) {
            strengths.add("Strong foundational technical background suitable for modern software engineering roles.");
        }
        result.setStrengths(strengths);

        List<String> recommendations = new ArrayList<>();
        if (!missingReq.isEmpty()) {
            recommendations.add("Consider highlighting or adding projects demonstrating experience with missing required skills: " + String.join(", ", missingReq.stream().limit(3).toList()));
        }
        if (!missingPref.isEmpty()) {
            recommendations.add("Familiarize yourself with nice-to-have technologies: " + String.join(", ", missingPref.stream().limit(3).toList()));
        }
        recommendations.add("Quantify project achievements with concrete business outcomes and metric impact (e.g., latency reduction, cost savings, user scale).");
        result.setRecommendations(recommendations);

        // Calculate alignment ratios
        int totalAssessments = assessments.size();
        long matchedCount = assessments.stream().filter(a -> "MATCHED".equalsIgnoreCase(a.getAssessmentStatus())).count();
        double ratio = totalAssessments > 0 ? (double) matchedCount / totalAssessments : 0.7;

        result.setExperienceAlignmentRatio(Math.max(0.4, Math.min(1.0, ratio + 0.1)));
        result.setQualificationsAlignmentRatio(Math.max(0.5, Math.min(1.0, ratio + 0.2)));

        result.setSummary(String.format(
                "Candidate shows relevant technical alignment with %d matching competencies identified against target specifications. Missing %d required competencies.",
                matching.size(), missingReq.size()
        ));
        result.setExperienceEvidenceNotes("Evaluated based on verifiable skill mentions and project descriptions in the submitted document.");
        result.setQualificationsNotes("Candidate profile matches engineering standards required for technical roles.");

        return result;
    }

    private boolean containsWord(String source, String word) {
        if (source == null || word == null || word.isBlank()) return false;
        // Escape special regex chars like C++, C#, etc.
        String regex = "\\b" + Pattern.quote(word) + "\\b";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source).find();
    }

    private Map<String, String> getTechnicalSkillsCatalog() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Java", "Language");
        map.put("Python", "Language");
        map.put("JavaScript", "Language");
        map.put("TypeScript", "Language");
        map.put("Go", "Language");
        map.put("C++", "Language");
        map.put("Spring Boot", "Framework");
        map.put("Spring Cloud", "Framework");
        map.put("Hibernate", "Framework");
        map.put("React", "Framework");
        map.put("Angular", "Framework");
        map.put("Node.js", "Framework");
        map.put("MySQL", "Database");
        map.put("PostgreSQL", "Database");
        map.put("MongoDB", "Database");
        map.put("Redis", "Database");
        map.put("Docker", "Cloud/DevOps");
        map.put("Kubernetes", "Cloud/DevOps");
        map.put("AWS", "Cloud/DevOps");
        map.put("Azure", "Cloud/DevOps");
        map.put("GCP", "Cloud/DevOps");
        map.put("CI/CD", "Cloud/DevOps");
        map.put("Kafka", "Distributed Systems");
        map.put("RabbitMQ", "Distributed Systems");
        map.put("REST API", "Architecture");
        map.put("Microservices", "Architecture");
        map.put("GraphQL", "Architecture");
        map.put("JUnit", "Testing");
        map.put("Git", "Tool");
        map.put("Linux", "Operating System");
        return map;
    }
}
