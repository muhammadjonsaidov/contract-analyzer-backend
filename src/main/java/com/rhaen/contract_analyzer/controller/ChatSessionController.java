package com.rhaen.contract_analyzer.controller;

import com.rhaen.contract_analyzer.dto.AnalyzerDTOs;
import com.rhaen.contract_analyzer.entity.ChatMessageEntity;
import com.rhaen.contract_analyzer.entity.ChatSession;
import com.rhaen.contract_analyzer.repository.ChatMessageRepository;
import com.rhaen.contract_analyzer.repository.ChatSessionRepository;
import com.rhaen.contract_analyzer.service.ContractChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class ChatSessionController {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ContractChatService chatService;

    public ChatSessionController(ChatSessionRepository sessionRepository, ChatMessageRepository messageRepository, ContractChatService chatService) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatSession> createSession(@RequestParam Long userId, @RequestParam String contractId, @RequestParam String title) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setContractId(contractId);
        session.setTitle(title);
        return ResponseEntity.ok(sessionRepository.save(session));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatSession>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(sessionRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        sessionRepository.deleteById(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}/history")
    public ResponseEntity<List<ChatMessageEntity>> getSessionHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(messageRepository.findBySessionIdOrderByPositionIndexAsc(sessionId));
    }

    @PutMapping("/{sessionId}/messages/{messageId}")
    public ResponseEntity<AnalyzerDTOs.ChatResponse> editMessage(
            @PathVariable String sessionId, 
            @PathVariable Long messageId, 
            @RequestBody AnalyzerDTOs.ChatRequest request) {
        String answer = chatService.editMessage(sessionId, messageId, request.question());
        return ResponseEntity.ok(new AnalyzerDTOs.ChatResponse(answer));
    }
}
