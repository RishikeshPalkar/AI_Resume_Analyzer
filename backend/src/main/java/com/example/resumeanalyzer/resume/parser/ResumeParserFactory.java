package com.example.resumeanalyzer.resume.parser;

import com.example.resumeanalyzer.common.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumeParserFactory {

    private final List<ResumeParser> parsers;

    public ResumeParserFactory(List<ResumeParser> parsers) {
        this.parsers = parsers;
    }

    public ResumeParser getParser(String contentType) {
        return parsers.stream()
                .filter(parser -> parser.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Unsupported document type: " + contentType + ". Supported types are PDF and DOCX."
                ));
    }
}
