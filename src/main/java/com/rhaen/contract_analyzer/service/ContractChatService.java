package com.rhaen.contract_analyzer.service;

import com.rhaen.contract_analyzer.entity.ChatMessageEntity;
import com.rhaen.contract_analyzer.entity.ChatSession;
import com.rhaen.contract_analyzer.repository.ChatMessageRepository;
import com.rhaen.contract_analyzer.repository.ChatSessionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ContractChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public ContractChatService(ChatClient.Builder chatClient, VectorStore vectorStore, ChatMemory chatMemory,
                               ChatMessageRepository chatMessageRepository, ChatSessionRepository chatSessionRepository) {
        this.chatClient = chatClient.build();
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    public String askQuestion(String contractId, String sessionId, String userQuestion) {
        return chatClient.prompt()
                .user(userQuestion)
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(sessionId)
                                .build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .filterExpression("contractId == '" + contractId + "'")
                                        .topK(5)
                                        .build())
                                .build()
                )
                .call()
                .content();
    }

    public String editMessage(String sessionId, Long messageId, String newContent) {
        ChatMessageEntity msg = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Truncate history from this message onwards
        chatMessageRepository.deleteBySessionIdAndPositionIndexGreaterThanEqual(sessionId, msg.getPositionIndex());
        
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
                
        // Regenerate response
        return askQuestion(session.getContractId(), sessionId, newContent);
    }
}
