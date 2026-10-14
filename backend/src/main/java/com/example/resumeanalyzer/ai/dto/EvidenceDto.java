package com.example.resumeanalyzer.ai.dto;

public class EvidenceDto {

    private String topic;
    private String quoteOrContext;
    private String status; // "SUPPORTED", "NOT_FOUND", "PARTIALLY_SUPPORTED"

    public EvidenceDto() {}

    public EvidenceDto(String topic, String quoteOrContext, String status) {
        this.topic = topic;
        this.quoteOrContext = quoteOrContext;
        this.status = status;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuoteOrContext() {
        return quoteOrContext;
    }

    public void setQuoteOrContext(String quoteOrContext) {
        this.quoteOrContext = quoteOrContext;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
