package com.example.resumeanalyzer.resume.parser;

import com.example.resumeanalyzer.common.exception.FileProcessingException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfResumeParser implements ResumeParser {

    private static final Logger log = LoggerFactory.getLogger(PdfResumeParser.class);

    @Override
    public boolean supports(String contentType) {
        return "application/pdf".equalsIgnoreCase(contentType);
    }

    @Override
    public String parse(InputStream inputStream) {
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(inputStream))) {
            if (document.isEncrypted()) {
                throw new FileProcessingException("Password-protected or encrypted PDF documents cannot be processed. Please upload an unencrypted file.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new FileProcessingException("Unable to extract readable text from PDF. Scanned images or empty documents are not supported without OCR.");
            }

            return text.trim();
        } catch (FileProcessingException e) {
            throw e;
        } catch (IOException e) {
            log.warn("Failed to parse PDF document: {}", e.getMessage());
            throw new FileProcessingException("Failed to read or parse PDF document: " + e.getMessage(), e);
        }
    }
}
