package com.example.resumeanalyzer.resume.controller;

import com.example.resumeanalyzer.common.response.ApiResponse;
import com.example.resumeanalyzer.common.response.PagedResponse;
import com.example.resumeanalyzer.resume.dto.ResumeDetailResponse;
import com.example.resumeanalyzer.resume.dto.ResumeResponse;
import com.example.resumeanalyzer.resume.service.ResumeService;
import com.example.resumeanalyzer.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resumes")
@Tag(name = "Resumes", description = "Endpoints for uploading, inspecting, and managing resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and parse a resume (PDF or DOCX)")
    public ResponseEntity<ApiResponse<ResumeDetailResponse>> uploadResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        ResumeDetailResponse response = resumeService.uploadAndExtractResume(principal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded and parsed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get current user's uploaded resumes with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ResumeResponse>>> getResumes(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int boundedSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(page, boundedSize, Sort.by("createdAt").descending());
        PagedResponse<ResumeResponse> response = resumeService.getResumesForUser(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed resume text and metadata by ID")
    public ResponseEntity<ApiResponse<ResumeDetailResponse>> getResumeById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ResumeDetailResponse response = resumeService.getResumeById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an owned resume and its associated analyses")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        resumeService.deleteResume(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.successMessage("Resume deleted successfully"));
    }
}
