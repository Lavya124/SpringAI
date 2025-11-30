package com.example.demo.repository;

import com.example.demo.entity.UserProfileMemoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileMemoryRepository extends JpaRepository<UserProfileMemoryEntity, Long> {

    List<UserProfileMemoryEntity> findByUserId(String userId);

    Optional<UserProfileMemoryEntity> findByUserIdAndMemoryKey(String userId, String memoryKey);
}
