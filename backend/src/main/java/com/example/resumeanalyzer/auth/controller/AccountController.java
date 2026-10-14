package com.example.resumeanalyzer.auth.controller;

import com.example.resumeanalyzer.auth.service.AuthService;
import com.example.resumeanalyzer.common.response.ApiResponse;
import com.example.resumeanalyzer.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@Tag(name = "Account", description = "Endpoints for managing user account and data deletion")
public class AccountController {

    private final AuthService authService;

    public AccountController(AuthService authService) {
        this.authService = authService;
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete account and all associated resumes, job descriptions, and analyses")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response) {
        authService.deleteAccount(principal.getId());
        return ResponseEntity.ok(ApiResponse.successMessage("Account and all associated data permanently deleted."));
    }
}
