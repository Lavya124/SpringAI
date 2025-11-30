package com.example.demo.repository;

import com.example.demo.entity.ChatMemoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMemoryRepository extends JpaRepository<ChatMemoryEntity, Long> {

    Optional<ChatMemoryEntity> findByUserIdAndChatId(String userId, String chatId);
}