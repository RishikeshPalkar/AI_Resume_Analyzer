package com.example.resumeanalyzer.resume.parser;

import java.io.InputStream;

public interface ResumeParser {
    boolean supports(String contentType);
    String parse(InputStream inputStream);
}
