package com.rhaen.contract_analyzer.service;

import com.rhaen.contract_analyzer.dto.ContractAnalysis;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ContractAnalyzerService {

    private final ChatClient chatClient;

    public ContractAnalyzerService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ContractAnalysis analyzeContract(String contractText) {
        return chatClient.prompt()
                .system("""
                        You are an expert enterprise legal AI. Analyze the provided contract text and extract key information. 
                        
                        1. Risk Score (0-100): Calculate the overall risk. 
                           - Base score is 10. 
                           - Add 20 points for missing termination clauses. 
                           - Add 30 points for uncapped liability or missing indemnification. 
                           - Add 15 points for auto-renewal without clear notice periods. 
                           - Add 10 points for unusually high late payment penalties.
                           - Cap the score at 100.
                        
                        2. Red Flags: List critical legal risks, such as unfair penalties, missing warranties, one-sided termination rights, or severe non-compete clauses. Be specific.
                        
                        3. Obligations: List the key responsibilities and deliverables of both parties. Focus on payment terms, services provided, and confidentiality.
                        
                        4. Critical Dates: Extract all explicit dates, terms of duration, deadlines, expiration dates, and renewal periods.
                        
                        Ensure the output is strictly structured as requested.
                        """)
                .user(contractText)
                // Spring AI automatically instructs the LLM to return JSON matching your Record
                .call()
                .entity(ContractAnalysis.class);
    }
}
