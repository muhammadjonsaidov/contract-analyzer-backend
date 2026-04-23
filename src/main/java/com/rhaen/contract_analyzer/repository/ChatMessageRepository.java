package com.rhaen.contract_analyzer.repository;

import com.rhaen.contract_analyzer.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findBySessionIdOrderByPositionIndexAsc(String sessionId);
    
    @Modifying
    @Transactional
    void deleteBySessionIdAndPositionIndexGreaterThanEqual(String sessionId, int positionIndex);
}
