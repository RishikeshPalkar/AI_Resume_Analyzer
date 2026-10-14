package com.example.resumeanalyzer.analysis.service;

import com.example.resumeanalyzer.ai.dto.AiAnalysisResultDto;
import com.example.resumeanalyzer.ai.dto.EvidenceDto;
import com.example.resumeanalyzer.ai.dto.KeywordMatchDto;
import com.example.resumeanalyzer.ai.dto.SkillAssessmentDto;
import com.example.resumeanalyzer.ai.service.AiAnalysisService;
import com.example.resumeanalyzer.analysis.dto.AnalysisDetailResponse;
import com.example.resumeanalyzer.analysis.dto.AnalysisResponse;
import com.example.resumeanalyzer.analysis.dto.CreateAnalysisRequest;
import com.example.resumeanalyzer.analysis.entity.Analysis;
import com.example.resumeanalyzer.analysis.entity.AnalysisResult;
import com.example.resumeanalyzer.analysis.entity.AnalysisStatus;
import com.example.resumeanalyzer.analysis.repository.AnalysisRepository;
import com.example.resumeanalyzer.analysis.repository.AnalysisResultRepository;
import com.example.resumeanalyzer.analysis.scoring.ScoreBreakdown;
import com.example.resumeanalyzer.analysis.scoring.ScoringService;
import com.example.resumeanalyzer.common.exception.BadRequestException;
import com.example.resumeanalyzer.common.exception.ResourceNotFoundException;
import com.example.resumeanalyzer.common.response.PagedResponse;
import com.example.resumeanalyzer.jobdescription.entity.JobDescription;
import com.example.resumeanalyzer.jobdescription.repository.JobDescriptionRepository;
import com.example.resumeanalyzer.resume.entity.Resume;
import com.example.resumeanalyzer.resume.repository.ResumeRepository;
import com.example.resumeanalyzer.user.entity.User;
import com.example.resumeanalyzer.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;
    private final AiAnalysisService aiAnalysisService;
    private final ScoringService scoringService;
    private final ObjectMapper objectMapper;
    private final String promptVersion;
    private final String modelIdentifier;

    public AnalysisService(
            AnalysisRepository analysisRepository,
            AnalysisResultRepository analysisResultRepository,
            ResumeRepository resumeRepository,
            JobDescriptionRepository jobDescriptionRepository,
            UserRepository userRepository,
            AiAnalysisService aiAnalysisService,
            ScoringService scoringService,
            ObjectMapper objectMapper,
            @Value("${app.ai.prompt-version:v1.2.0}") String promptVersion,
            @Value("${app.ai.model:gpt-4o-mini}") String modelIdentifier) {
        this.analysisRepository = analysisRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.userRepository = userRepository;
        this.aiAnalysisService = aiAnalysisService;
        this.scoringService = scoringService;
        this.objectMapper = objectMapper;
        this.promptVersion = promptVersion;
        this.modelIdentifier = modelIdentifier;
    }

    @Transactional
    public AnalysisDetailResponse createAndExecuteAnalysis(Long userId, CreateAnalysisRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Resume resume = resumeRepository.findByIdAndUserId(request.getResumeId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", request.getResumeId()));

        JobDescription jobDescription;
        if (request.getJobDescriptionId() != null) {
            jobDescription = jobDescriptionRepository.findByIdAndUserId(request.getJobDescriptionId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("JobDescription", "id", request.getJobDescriptionId()));
        } else if (request.getJobDescriptionText() != null && !request.getJobDescriptionText().isBlank()) {
            jobDescription = new JobDescription(
                    user,
                    request.getJobTitle(),
                    request.getCompany(),
                    request.getJobDescriptionText().trim()
            );
            jobDescription = jobDescriptionRepository.save(jobDescription);
        } else {
            throw new BadRequestException("Either jobDescriptionId or jobDescriptionText must be provided");
        }

        Analysis analysis = new Analysis(user, resume, jobDescription, promptVersion);
        analysis.setStatus(AnalysisStatus.PROCESSING);
        analysis.setModelIdentifier(modelIdentifier);
        analysis = analysisRepository.save(analysis);

        try {
            // Step 1: AI semantic analysis and requirement extraction
            AiAnalysisResultDto aiResult = aiAnalysisService.analyze(
                    resume.getExtractedText(),
                    jobDescription.getDescriptionText()
            );

            // Step 2: Deterministic backend scoring
            ScoreBreakdown breakdown = scoringService.calculateScore(aiResult);

            // Step 3: Populate AnalysisResult
            AnalysisResult result = new AnalysisResult();
            result.setSummary(aiResult.getSummary());
            result.setMatchingSkillsJson(toJson(aiResult.getMatchingSkills()));
            result.setMissingRequiredSkillsJson(toJson(aiResult.getMissingRequiredSkills()));
            result.setMissingPreferredSkillsJson(toJson(aiResult.getMissingPreferredSkills()));
            result.setStrengthsJson(toJson(aiResult.getStrengths()));
            result.setRecommendationsJson(toJson(aiResult.getRecommendations()));
            result.setKeywordAnalysisJson(toJson(aiResult.getKeywordAnalysis()));
            result.setEvidenceJson(toJson(aiResult.getEvidence()));

            analysis.setResult(result);
            analysis.setMatchScore(breakdown.getOverallScore());
            analysis.setStatus(AnalysisStatus.COMPLETED);
            analysis.setCompletedAt(Instant.now());

            analysis = analysisRepository.save(analysis);

            return mapToDetailResponse(analysis, breakdown, aiResult.getSkillAssessments());
        } catch (Exception e) {
            log.error("Analysis execution failed for ID: {}", analysis.getId(), e);
            analysis.setStatus(AnalysisStatus.FAILED);
            analysis.setErrorCode(e.getClass().getSimpleName());
            analysisRepository.save(analysis);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public PagedResponse<AnalysisResponse> getAnalysesForUser(Long userId, Pageable pageable) {
        Page<Analysis> page = analysisRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<AnalysisResponse> content = page.getContent().stream()
                .map(a -> new AnalysisResponse(
                        a.getId(),
                        a.getResume().getId(),
                        a.getResume().getOriginalFilename(),
                        a.getJobDescription().getId(),
                        a.getJobDescription().getTitle(),
                        a.getJobDescription().getCompany(),
                        a.getMatchScore(),
                        a.getStatus(),
                        a.getCreatedAt()
                ))
                .toList();

        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional(readOnly = true)
    public AnalysisDetailResponse getAnalysisById(Long id, Long userId) {
        Analysis analysis = analysisRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis", "id", id));

        return mapEntityToDetailResponse(analysis);
    }

    @Transactional
    public void deleteAnalysis(Long id, Long userId) {
        Analysis analysis = analysisRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis", "id", id));
        analysisRepository.delete(analysis);
    }

    @Transactional(readOnly = true)
    public long countAnalysesForUser(Long userId) {
        return analysisRepository.countByUserId(userId);
    }

    private AnalysisDetailResponse mapToDetailResponse(Analysis analysis, ScoreBreakdown breakdown, List<SkillAssessmentDto> assessments) {
        AnalysisDetailResponse res = new AnalysisDetailResponse();
        res.setId(analysis.getId());
        res.setResumeId(analysis.getResume().getId());
        res.setResumeFilename(analysis.getResume().getOriginalFilename());
        res.setJobDescriptionId(analysis.getJobDescription().getId());
        res.setJobTitle(analysis.getJobDescription().getTitle());
        res.setCompany(analysis.getJobDescription().getCompany());
        res.setStatus(analysis.getStatus());
        res.setMatchScore(analysis.getMatchScore());
        res.setScoreBreakdown(breakdown);

        AnalysisResult result = analysis.getResult();
        if (result != null) {
            res.setSummary(result.getSummary());
            res.setMatchingSkills(fromJson(result.getMatchingSkillsJson(), new TypeReference<>() {}));
            res.setMissingRequiredSkills(fromJson(result.getMissingRequiredSkillsJson(), new TypeReference<>() {}));
            res.setMissingPreferredSkills(fromJson(result.getMissingPreferredSkillsJson(), new TypeReference<>() {}));
            res.setStrengths(fromJson(result.getStrengthsJson(), new TypeReference<>() {}));
            res.setRecommendations(fromJson(result.getRecommendationsJson(), new TypeReference<>() {}));
            res.setKeywordAnalysis(fromJson(result.getKeywordAnalysisJson(), new TypeReference<>() {}));
            res.setEvidence(fromJson(result.getEvidenceJson(), new TypeReference<>() {}));
        }

        res.setSkillAssessments(assessments != null ? assessments : new ArrayList<>());
        res.setModelIdentifier(analysis.getModelIdentifier());
        res.setPromptVersion(analysis.getPromptVersion());
        res.setCreatedAt(analysis.getCreatedAt());
        res.setCompletedAt(analysis.getCompletedAt());
        return res;
    }

    private AnalysisDetailResponse mapEntityToDetailResponse(Analysis analysis) {
        ScoreBreakdown breakdown = new ScoreBreakdown();
        breakdown.setOverallScore(analysis.getMatchScore());

        AnalysisResult result = analysis.getResult();
        List<SkillAssessmentDto> assessments = new ArrayList<>();

        if (result != null) {
            List<String> matching = fromJson(result.getMatchingSkillsJson(), new TypeReference<>() {});
            List<String> missingReq = fromJson(result.getMissingRequiredSkillsJson(), new TypeReference<>() {});
            List<String> missingPref = fromJson(result.getMissingPreferredSkillsJson(), new TypeReference<>() {});

            for (String s : matching) {
                assessments.add(new SkillAssessmentDto(s, "Technical Competency", "REQUIRED", "MATCHED", "Found in resume", "Matches job requirements"));
            }
            for (String s : missingReq) {
                assessments.add(new SkillAssessmentDto(s, "Required Requirement", "REQUIRED", "MISSING", "Not found in resume", "Required competency"));
            }
            for (String s : missingPref) {
                assessments.add(new SkillAssessmentDto(s, "Preferred Requirement", "PREFERRED", "MISSING", "Not found in resume", "Nice to have"));
            }
        }

        return mapToDetailResponse(analysis, breakdown, assessments);
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    private <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            return null;
        }
    }
}
