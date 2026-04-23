package com.rhaen.contract_analyzer.controller;

import com.rhaen.contract_analyzer.dto.AnalyzerDTOs;
import com.rhaen.contract_analyzer.service.ContractAnalyzerService;
import com.rhaen.contract_analyzer.service.ContractChatService;
import com.rhaen.contract_analyzer.service.ContractIngestionService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    private static final Logger log = LoggerFactory.getLogger(ContractController.class);
    private final ContractIngestionService ingestionService;
    private final ContractAnalyzerService analyzerService;
    private final ContractChatService chatService;
    private final com.rhaen.contract_analyzer.repository.ChatSessionRepository chatSessionRepository;

    public ContractController(
            ContractIngestionService ingestionService,
            ContractAnalyzerService analyzerService,
            ContractChatService chatService,
            com.rhaen.contract_analyzer.repository.ChatSessionRepository chatSessionRepository) {
        this.ingestionService = ingestionService;
        this.analyzerService = analyzerService;
        this.chatService = chatService;
        this.chatSessionRepository = chatSessionRepository;
    }

    // Endpoint 1: Upload PDF, chunk to pgvector, and analyze risks
    @PostMapping("/upload")
    public ResponseEntity<AnalyzerDTOs.UploadResponse> uploadAndAnalyze(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Generate a unique ID for this contract
            String contractId = UUID.randomUUID().toString();

            // 2. Extract text manually here so we can pass it to both services
            String extractedText;
            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                extractedText = stripper.getText(document);
            }

            // 3. Save to Vector Database (for later Q&A)
            ingestionService.processAndStoreContract(extractedText, contractId);

            // 4. Run Risk Analysis using LLM Structured Output
            var analysis = analyzerService.analyzeContract(extractedText);

            // 5. Build and return the response
            AnalyzerDTOs.UploadResponse response = new AnalyzerDTOs.UploadResponse(
                    contractId,
                    "Contract processed successfully!",
                    analysis.riskScore(),
                    analysis.redFlags(),
                    analysis.obligations(),
                    analysis.criticalDates()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint 2: Ask questions about a specific contract via Session (RAG)
    @PostMapping("/{sessionId}/chat")
    public ResponseEntity<AnalyzerDTOs.ChatResponse> chatWithContract(
            @PathVariable String sessionId,
            @RequestBody AnalyzerDTOs.ChatRequest request) {
        try {
            // Find contract ID from session
            com.rhaen.contract_analyzer.entity.ChatSession session = 
                chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
                
            // Call the chat service which uses pgvector to find the answer
            String answer = chatService.askQuestion(session.getContractId(), sessionId, request.question());

            return ResponseEntity.ok(new AnalyzerDTOs.ChatResponse(answer));
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
