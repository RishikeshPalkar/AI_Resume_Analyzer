package com.example.resumeanalyzer.jobdescription.controller;

import com.example.resumeanalyzer.common.response.ApiResponse;
import com.example.resumeanalyzer.common.response.PagedResponse;
import com.example.resumeanalyzer.jobdescription.dto.JobDescriptionRequest;
import com.example.resumeanalyzer.jobdescription.dto.JobDescriptionResponse;
import com.example.resumeanalyzer.jobdescription.service.JobDescriptionService;
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

@RestController
@RequestMapping("/api/v1/job-descriptions")
@Tag(name = "Job Descriptions", description = "Endpoints for submitting and managing target job requirements")
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    public JobDescriptionController(JobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    @PostMapping
    @Operation(summary = "Submit a new job description")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> createJobDescription(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody JobDescriptionRequest request) {
        JobDescriptionResponse response = jobDescriptionService.createJobDescription(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Job description saved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get user's previous job descriptions with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<JobDescriptionResponse>>> getJobDescriptions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int boundedSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(page, boundedSize, Sort.by("createdAt").descending());
        PagedResponse<JobDescriptionResponse> response = jobDescriptionService.getJobDescriptionsForUser(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job description by ID")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> getJobDescriptionById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        JobDescriptionResponse response = jobDescriptionService.getJobDescriptionById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an owned job description")
    public ResponseEntity<ApiResponse<Void>> deleteJobDescription(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        jobDescriptionService.deleteJobDescription(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.successMessage("Job description deleted successfully"));
    }
}
