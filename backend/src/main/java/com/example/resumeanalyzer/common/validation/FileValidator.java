package com.example.resumeanalyzer.common.validation;

import com.example.resumeanalyzer.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class FileValidator {

    public static final String PDF_MIME = "application/pdf";
    public static final String DOCX_MIME = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final long maxFileSizeBytes;
    private final Pattern safeFilenamePattern = Pattern.compile("^[a-zA-Z0-9._ -]+$");

    public FileValidator(@Value("${app.upload.max-file-size-bytes:5242880}") long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public String validateAndDetectContentType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new BadRequestException("File size exceeds maximum allowed limit of " + (maxFileSizeBytes / (1024 * 1024)) + " MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("Filename cannot be blank");
        }

        // Check magic bytes / signature
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int read = is.read(header);
            if (read < 4) {
                throw new BadRequestException("Corrupt or invalid document header");
            }

            // PDF Magic Bytes: %PDF (0x25 0x50 0x44 0x46)
            if (header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46) {
                return PDF_MIME;
            }

            // ZIP / DOCX Magic Bytes: PK\x03\x04 (0x50 0x4B 0x03 0x04)
            if (header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04) {
                if (originalFilename.toLowerCase().endsWith(".docx")) {
                    return DOCX_MIME;
                }
            }

            throw new BadRequestException("Unsupported or invalid file format. Only PDF and DOCX files are allowed.");
        } catch (IOException e) {
            throw new BadRequestException("Failed to read uploaded file signature: " + e.getMessage());
        }
    }

    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return "resume_upload";
        }
        // Remove path traversal characters
        String clean = filename.replace("\\", "/");
        int lastSlash = clean.lastIndexOf('/');
        if (lastSlash >= 0) {
            clean = clean.substring(lastSlash + 1);
        }
        // Remove unsafe characters
        clean = clean.replaceAll("[^a-zA-Z0-9._ -]", "_").trim();
        if (clean.isBlank()) {
            clean = "resume_document";
        }
        if (clean.length() > 150) {
            clean = clean.substring(clean.length() - 150);
        }
        return clean;
    }
}
