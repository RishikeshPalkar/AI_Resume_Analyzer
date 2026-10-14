package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.analysis.dto.CreateAnalysisRequest;
import com.example.resumeanalyzer.auth.dto.AuthResponse;
import com.example.resumeanalyzer.auth.dto.RegisterRequest;
import com.example.resumeanalyzer.common.response.ApiResponse;
import com.example.resumeanalyzer.resume.entity.Resume;
import com.example.resumeanalyzer.resume.repository.ResumeRepository;
import com.example.resumeanalyzer.user.entity.User;
import com.example.resumeanalyzer.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalysisControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private com.example.resumeanalyzer.analysis.repository.AnalysisRepository analysisRepository;

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

    private String registerAndGetToken(String name, String email) throws Exception {
        RegisterRequest regReq = new RegisterRequest(name, email, "Passw0rd123!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<AuthResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );
        return response.getData().getAccessToken();
    }

    @Test
    @DisplayName("Should execute analysis and calculate match score successfully")
    void testCreateAnalysisFlow() throws Exception {
        String token = registerAndGetToken("John Developer", "john@example.com");
        User user = userRepository.findByEmail("john@example.com").orElseThrow();

        // Create resume for John
        Resume resume = new Resume(
                user, "john_resume.pdf", "application/pdf",
                "Senior Java Engineer with 5 years experience in Spring Boot, MySQL, REST API, Docker, AWS and Microservices architecture.",
                "hash123"
        );
        resume = resumeRepository.save(resume);

        CreateAnalysisRequest analysisReq = new CreateAnalysisRequest(
                resume.getId(),
                null,
                "Senior Java Developer",
                "Tech Corp",
                "Looking for a Senior Java Developer with expertise in Spring Boot, Microservices, Docker, and MySQL."
        );

        mockMvc.perform(post("/api/v1/analyses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(analysisReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.matchScore", notNullValue()))
                .andExpect(jsonPath("$.data.matchingSkills", hasItem("Java")))
                .andExpect(jsonPath("$.data.matchingSkills", hasItem("Spring Boot")))
                .andExpect(jsonPath("$.data.matchingSkills", hasItem("Docker")))
                .andExpect(jsonPath("$.data.scoreBreakdown", notNullValue()));
    }

    @Test
    @DisplayName("Cross-user isolation: User B cannot access User A's analysis report")
    void testCrossUserAccessForbidden() throws Exception {
        String userAToken = registerAndGetToken("User A", "user_a@example.com");
        String userBToken = registerAndGetToken("User B", "user_b@example.com");

        User userA = userRepository.findByEmail("user_a@example.com").orElseThrow();
        Resume resumeA = new Resume(
                userA, "a_resume.pdf", "application/pdf",
                "Java backend developer with Spring Boot experience.",
                "hashA"
        );
        resumeA = resumeRepository.save(resumeA);

        CreateAnalysisRequest req = new CreateAnalysisRequest(
                resumeA.getId(), null, "Java Dev", "Company A",
                "Seeking Java backend engineer with Spring Boot knowledge."
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/analyses")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<Object> res = objectMapper.readValue(createResult.getResponse().getContentAsString(), new TypeReference<>() {});
        // Get created analysis id
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) res.getData();
        Number analysisId = (Number) data.get("id");

        // User B tries to view User A's analysis -> Should return 404 (Resource Not Found for User B)
        mockMvc.perform(get("/api/v1/analyses/" + analysisId)
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }
}
