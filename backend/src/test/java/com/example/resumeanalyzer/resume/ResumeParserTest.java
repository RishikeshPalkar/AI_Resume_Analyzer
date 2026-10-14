package com.example.resumeanalyzer.resume;

import com.example.resumeanalyzer.common.exception.FileProcessingException;
import com.example.resumeanalyzer.resume.parser.DocxResumeParser;
import com.example.resumeanalyzer.resume.parser.PdfResumeParser;
import com.example.resumeanalyzer.resume.parser.ResumeParserFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResumeParserTest {

    private PdfResumeParser pdfParser;
    private DocxResumeParser docxParser;
    private ResumeParserFactory factory;

    @BeforeEach
    void setUp() {
        pdfParser = new PdfResumeParser();
        docxParser = new DocxResumeParser();
        factory = new ResumeParserFactory(List.of(pdfParser, docxParser));
    }

    @Test
    @DisplayName("Should extract text from a valid PDF document")
    void testPdfTextExtraction() throws IOException {
        byte[] pdfBytes;
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                stream.beginText();
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                stream.newLineAtOffset(50, 700);
                stream.showText("Senior Java Backend Engineer with Spring Boot and AWS experience");
                stream.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            pdfBytes = out.toByteArray();
        }

        String extracted = pdfParser.parse(new ByteArrayInputStream(pdfBytes));
        assertNotNull(extracted);
        assertTrue(extracted.contains("Senior Java Backend Engineer"));
    }

    @Test
    @DisplayName("Should extract text from a valid DOCX document")
    void testDocxTextExtraction() throws IOException {
        byte[] docxBytes;
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            run.setText("Full Stack Developer skilled in React, TypeScript and MySQL");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            docxBytes = out.toByteArray();
        }

        String extracted = docxParser.parse(new ByteArrayInputStream(docxBytes));
        assertNotNull(extracted);
        assertTrue(extracted.contains("Full Stack Developer"));
    }

    @Test
    @DisplayName("Should throw FileProcessingException when parsing corrupt PDF bytes")
    void testCorruptPdfRejection() {
        byte[] corruptBytes = "Not a real PDF document".getBytes();
        assertThrows(FileProcessingException.class, () ->
                pdfParser.parse(new ByteArrayInputStream(corruptBytes)));
    }

    @Test
    @DisplayName("Factory should return the appropriate parser based on MIME type")
    void testFactorySelection() {
        assertEquals(pdfParser, factory.getParser("application/pdf"));
        assertEquals(docxParser, factory.getParser(DocxResumeParser.DOCX_MIME_TYPE));
    }
}
