package com.example.demo.memory;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserProfileMemory {

    // userId -> (key -> value)
    private final Map<String, Map<String, String>> memory = new HashMap<>();

    public void save(String userId, String key, String value) {
        memory.computeIfAbsent(userId, u -> new HashMap<>())
                .put(key, value);
    }

    public Map<String, String> getProfile(String userId) {
        return memory.getOrDefault(userId, new HashMap<>());
    }
}
