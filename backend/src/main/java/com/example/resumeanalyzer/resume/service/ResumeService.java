package com.example.resumeanalyzer.resume.service;

import com.example.resumeanalyzer.common.exception.BadRequestException;
import com.example.resumeanalyzer.common.exception.FileProcessingException;
import com.example.resumeanalyzer.common.exception.ResourceNotFoundException;
import com.example.resumeanalyzer.common.response.PagedResponse;
import com.example.resumeanalyzer.common.validation.FileValidator;
import com.example.resumeanalyzer.resume.dto.ResumeDetailResponse;
import com.example.resumeanalyzer.resume.dto.ResumeResponse;
import com.example.resumeanalyzer.resume.entity.Resume;
import com.example.resumeanalyzer.resume.parser.ResumeParser;
import com.example.resumeanalyzer.resume.parser.ResumeParserFactory;
import com.example.resumeanalyzer.resume.repository.ResumeRepository;
import com.example.resumeanalyzer.user.entity.User;
import com.example.resumeanalyzer.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileValidator fileValidator;
    private final ResumeParserFactory resumeParserFactory;
    private final int maxExtractedTextChars;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            FileValidator fileValidator,
            ResumeParserFactory resumeParserFactory,
            @Value("${app.upload.max-extracted-text-chars:50000}") int maxExtractedTextChars) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.fileValidator = fileValidator;
        this.resumeParserFactory = resumeParserFactory;
        this.maxExtractedTextChars = maxExtractedTextChars;
    }

    @Transactional
    public ResumeDetailResponse uploadAndExtractResume(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String detectedContentType = fileValidator.validateAndDetectContentType(file);
        String sanitizedFilename = fileValidator.sanitizeFilename(file.getOriginalFilename());

        ResumeParser parser = resumeParserFactory.getParser(detectedContentType);
        String extractedText;

        try (InputStream is = file.getInputStream()) {
            extractedText = parser.parse(is);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read file stream: " + e.getMessage(), e);
        }

        if (extractedText == null || extractedText.trim().length() < 30) {
            throw new BadRequestException("The document does not contain sufficient readable text. Please upload a valid text-readable resume (not an image-only scan).");
        }

        if (extractedText.length() > maxExtractedTextChars) {
            extractedText = extractedText.substring(0, maxExtractedTextChars);
        }

        String contentHash = calculateSha256(extractedText);

        Resume resume = new Resume(user, sanitizedFilename, detectedContentType, extractedText, contentHash);
        resume = resumeRepository.save(resume);

        return new ResumeDetailResponse(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getDetectedContentType(),
                resume.getExtractedText(),
                resume.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public PagedResponse<ResumeResponse> getResumesForUser(Long userId, Pageable pageable) {
        Page<Resume> page = resumeRepository.findByUserId(userId, pageable);
        List<ResumeResponse> content = page.getContent().stream()
                .map(r -> new ResumeResponse(
                        r.getId(),
                        r.getOriginalFilename(),
                        r.getDetectedContentType(),
                        r.getExtractedText().length(),
                        r.getCreatedAt()
                ))
                .toList();

        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional(readOnly = true)
    public ResumeDetailResponse getResumeById(Long id, Long userId) {
        Resume resume = resumeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));

        return new ResumeDetailResponse(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getDetectedContentType(),
                resume.getExtractedText(),
                resume.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Resume getResumeEntity(Long id, Long userId) {
        return resumeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));
    }

    @Transactional
    public void deleteResume(Long id, Long userId) {
        Resume resume = resumeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));
        resumeRepository.delete(resume);
    }

    private String calculateSha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
