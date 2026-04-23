package com.rhaen.contract_analyzer.dto;

import java.util.List;

public record ContractAnalysis(
        int riskScore,
        List<String> redFlags,
        List<String> obligations,
        List<String> criticalDates
) {
}
