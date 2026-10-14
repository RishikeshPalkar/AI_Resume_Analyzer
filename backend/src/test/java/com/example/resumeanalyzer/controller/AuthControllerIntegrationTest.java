package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.auth.dto.LoginRequest;
import com.example.resumeanalyzer.auth.dto.RegisterRequest;
import com.example.resumeanalyzer.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.resumeanalyzer.analysis.repository.AnalysisRepository analysisRepository;

    @Autowired
    private com.example.resumeanalyzer.resume.repository.ResumeRepository resumeRepository;

    @Autowired
    private com.example.resumeanalyzer.jobdescription.repository.JobDescriptionRepository jobDescriptionRepository;

    @Autowired
    private com.example.resumeanalyzer.user.repository.RefreshSessionRepository refreshSessionRepository;

    @BeforeEach
    void cleanUp() {
        analysisRepository.deleteAll();
        resumeRepository.deleteAll();
        jobDescriptionRepository.deleteAll();
        refreshSessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register should create account and return token")
    void testRegisterUser() throws Exception {
        RegisterRequest req = new RegisterRequest("Test User", "test@example.com", "SecurePassword123!");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.email", is("test@example.com")))
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login should authenticate valid credentials")
    void testLoginUser() throws Exception {
        // Register first
        RegisterRequest regReq = new RegisterRequest("Login User", "login@example.com", "Password123!");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isOk());

        // Then login
        LoginRequest loginReq = new LoginRequest("login@example.com", "Password123!");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.name", is("Login User")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me should return 401 when token is missing")
    void testUnauthenticatedProfile() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }
}
