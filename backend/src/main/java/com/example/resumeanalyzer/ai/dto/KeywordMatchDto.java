package com.example.resumeanalyzer.ai.dto;

public class KeywordMatchDto {

    private String keyword;
    private boolean foundInResume;
    private String context;

    public KeywordMatchDto() {}

    public KeywordMatchDto(String keyword, boolean foundInResume, String context) {
        this.keyword = keyword;
        this.foundInResume = foundInResume;
        this.context = context;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isFoundInResume() {
        return foundInResume;
    }

    public void setFoundInResume(boolean foundInResume) {
        this.foundInResume = foundInResume;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
