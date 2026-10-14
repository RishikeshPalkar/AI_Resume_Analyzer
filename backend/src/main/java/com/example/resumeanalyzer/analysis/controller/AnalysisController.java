package com.example.resumeanalyzer.analysis.controller;

import com.example.resumeanalyzer.analysis.dto.AnalysisDetailResponse;
import com.example.resumeanalyzer.analysis.dto.AnalysisResponse;
import com.example.resumeanalyzer.analysis.dto.CreateAnalysisRequest;
import com.example.resumeanalyzer.analysis.service.AnalysisService;
import com.example.resumeanalyzer.common.response.ApiResponse;
import com.example.resumeanalyzer.common.response.PagedResponse;
import com.example.resumeanalyzer.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analyses")
@Tag(name = "Analyses", description = "Endpoints for triggering AI resume analysis and retrieving reports")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    @Operation(summary = "Execute AI analysis on a resume against a target job description")
    public ResponseEntity<ApiResponse<AnalysisDetailResponse>> createAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateAnalysisRequest request) {
        AnalysisDetailResponse response = analysisService.createAndExecuteAnalysis(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Analysis completed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get current user's analysis history with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<AnalysisResponse>>> getAnalyses(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int boundedSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(page, boundedSize, Sort.by("createdAt").descending());
        PagedResponse<AnalysisResponse> response = analysisService.getAnalysesForUser(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user activity statistics for the dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        long count = analysisService.countAnalysesForUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("totalAnalyses", count)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full analysis report by ID")
    public ResponseEntity<ApiResponse<AnalysisDetailResponse>> getAnalysisById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        AnalysisDetailResponse response = analysisService.getAnalysisById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an owned analysis report")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        analysisService.deleteAnalysis(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.successMessage("Analysis deleted successfully"));
    }
}
