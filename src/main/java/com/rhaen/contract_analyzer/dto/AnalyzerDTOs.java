package com.rhaen.contract_analyzer.dto;

import java.util.List;

public class AnalyzerDTOs {
    // What Streamlit sends when asking a question
    public record ChatRequest(String question) {}

    // What the backend returns for a chat answer
    public record ChatResponse(String answer) {}

    // What the backend returns after a successful upload
    public record UploadResponse(
            String contractId,
            String message,
            int riskScore,
            List<String> redFlags,
            List<String> obligations,
            List<String> criticalDates
    ) {}

}
