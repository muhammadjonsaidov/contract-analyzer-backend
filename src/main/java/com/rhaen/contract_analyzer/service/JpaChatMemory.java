package com.rhaen.contract_analyzer.service;

import com.rhaen.contract_analyzer.entity.ChatMessageEntity;
import com.rhaen.contract_analyzer.repository.ChatMessageRepository;
import jakarta.annotation.Nonnull;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JpaChatMemory implements ChatMemory {

    private final ChatMessageRepository repository;

    public JpaChatMemory(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void add(@Nonnull String conversationId, @Nonnull Message message) {
        add(conversationId, List.of(message));
    }

    @Override
    public void add(@Nonnull String conversationId, List<Message> messages) {
        List<ChatMessageEntity> existing = repository.findBySessionIdOrderByPositionIndexAsc(conversationId);
        int startIndex = existing.size();
        
        for (Message msg : messages) {
            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setSessionId(conversationId);
            entity.setRole(msg.getMessageType());
            entity.setContent(msg.getText());
            entity.setPositionIndex(startIndex++);
            repository.save(entity);
        }
    }

    // This method does not exist in the ChatMemory interface for this version of Spring AI
    public List<Message> get(String conversationId, int lastN) {
        List<ChatMessageEntity> entities = repository.findBySessionIdOrderByPositionIndexAsc(conversationId);
        
        // Return only last N messages
        int start = Math.max(0, entities.size() - lastN);
        List<ChatMessageEntity> subset = entities.subList(start, entities.size());
        
        return subset.stream().map(e -> {
            if (e.getRole() == MessageType.USER) {
                return new UserMessage(e.getContent());
            } else {
                return new AssistantMessage(e.getContent());
            }
        }).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<Message> get(@Nonnull String conversationId) {
        return get(conversationId, 100); // Default to last 100 messages
    }

    @Override
    public void clear(@Nonnull String conversationId) {
        repository.deleteBySessionIdAndPositionIndexGreaterThanEqual(conversationId, 0);
    }
}
