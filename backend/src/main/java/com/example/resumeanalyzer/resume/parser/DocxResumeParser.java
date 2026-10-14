package com.example.resumeanalyzer.resume.parser;

import com.example.resumeanalyzer.common.exception.FileProcessingException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class DocxResumeParser implements ResumeParser {

    private static final Logger log = LoggerFactory.getLogger(DocxResumeParser.class);

    public static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @Override
    public boolean supports(String contentType) {
        return DOCX_MIME_TYPE.equalsIgnoreCase(contentType);
    }

    @Override
    public String parse(InputStream inputStream) {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String text = extractor.getText();

            if (text == null || text.trim().isEmpty()) {
                throw new FileProcessingException("DOCX file does not contain readable text.");
            }

            return text.trim();
        } catch (FileProcessingException e) {
            throw e;
        } catch (IOException e) {
            log.warn("Failed to parse DOCX document: {}", e.getMessage());
            throw new FileProcessingException("Failed to read or parse DOCX document: " + e.getMessage(), e);
        } catch (Exception e) {
            log.warn("Corrupt or invalid DOCX document: {}", e.getMessage());
            throw new FileProcessingException("Corrupted or invalid DOCX file: " + e.getMessage(), e);
        }
    }
}
