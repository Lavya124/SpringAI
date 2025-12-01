package com.example.demo.service;

import com.example.demo.entity.ChatMemoryEntity;
import com.example.demo.entity.UserProfileMemoryEntity;
import com.example.demo.memory.UserProfileMemory;
import com.example.demo.repository.ChatMemoryRepository;
import com.example.demo.repository.UserProfileMemoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final Path memoryFile = Paths.get("memory.txt");
//    private final Map<String, String> memoryStore = new HashMap<>();
private final Map<String, Map<String, String>> memoryStore = new HashMap<>();
    private final Map<String, Map<String, String>> chatMemoryStore = new HashMap<>();
    private final UserProfileMemory profileMemory;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserProfileMemoryRepository userProfileRepo;
    private final ChatMemoryRepository chatMemoryRepo;




    // Stateless
//    public String askGemini(String message) {
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of(
//                                "parts", List.of(
//                                        Map.of("text", message)
//                                )
//                        )
//                )
//        );
//
//        String jsonResponse = webClient.post()
//                .uri("/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        return extractText(jsonResponse);
//    }


    //Store the data in the file
//    public String askGemini(String message) {
//
//        String memory = readMemory();
//
//        String combinedPrompt = memory + "\nUser: " + message;
//
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of(
//                                "parts", List.of(
//                                        Map.of("text", combinedPrompt)
//                                )
//                        )
//                )
//        );
//
//        String jsonResponse = webClient.post()
//                .uri("/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        String botReply = extractText(jsonResponse);
//
//        // UPDATE MEMORY
//        String newMemory = combinedPrompt + "\nBot: " + botReply + "\n";
//        writeMemory(newMemory);
//
//        return botReply;
//    }
//
//    private String readMemory() {
//        try {
//            if (Files.exists(memoryFile)) {
//                return Files.readString(memoryFile);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    private void writeMemory(String newEntry) {
//        try {
//            Files.writeString(memoryFile, newEntry,
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.WRITE,
//                    StandardOpenOption.TRUNCATE_EXISTING
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //Per session
//    public String askGemini(String message, HttpSession session) {
//
//        String sessionId = session.getId();
//
//        // get old memory or empty
//        String memory = memoryStore.getOrDefault(sessionId, "");
//
//        String combinedPrompt = memory + "\nUser: " + message;
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of(
//                                "parts", List.of(
//                                        Map.of("text", combinedPrompt)
//                                )
//                        )
//                )
//        );
//
//        String jsonResponse = webClient.post()
//                .uri("/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        String botReply = extractText(jsonResponse);
//
//        // Update session memory
//        String newMemory = combinedPrompt + "\nBot: " + botReply + "\n";
//        memoryStore.put(sessionId, newMemory);
//
//        return botReply;
//    }

    //per user  has many chat id's
//    public String askGemini(String userId, String chatId, String message) {
//
//        //  Get memory map for this user
//        Map<String, String> userChats =
//                memoryStore.computeIfAbsent(userId, k -> new HashMap<>());
//
//        // Get memory for this chatId
//        String memory = userChats.getOrDefault(chatId, "");
//
//        // Build combined prompt
//        String combinedPrompt = memory + "\nUser: " + message;
//
//        // Make Gemini request
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of(
//                                "parts", List.of(
//                                        Map.of("text", combinedPrompt)
//                                )
//                        )
//                )
//        );
//
//        String jsonResponse = webClient.post()
//                .uri("/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        String botReply = extractText(jsonResponse);
//
//        // Update memory
//        String newMemory = combinedPrompt + "\nBot: " + botReply + "\n";
//        userChats.put(chatId, newMemory);
//
//        return botReply;
//    }

    public String askGemini(String userId, String chatId, String message) {

        // 1) Load chat memory for this user + chat
//        Map<String, String> userChats =
//                chatMemoryStore.computeIfAbsent(userId, u -> new HashMap<>());
//        String history = userChats.getOrDefault(chatId, "");
        String history = chatMemoryRepo
                .findByUserIdAndChatId(userId, chatId)
                .map(ChatMemoryEntity::getHistory)
                .orElse("");


        // 2) Let Gemini decide if this message contains important personal info
        String memoryJsonString = extractMemoryUsingGemini(message);
        storeProfileFromJson(userId, memoryJsonString);

        // 3) Build user profile text from global memory
//        Map<String, String> profile = profileMemory.getProfile(userId);

        Map<String, String> profile = new HashMap<>();

        userProfileRepo.findByUserId(userId).forEach(row -> {
            profile.put(row.getMemoryKey(), row.getMemoryValue());
        });

        StringBuilder profileText = new StringBuilder();
        if (!profile.isEmpty()) {
            profileText.append("User profile:\n");
            profile.forEach((k, v) ->
                    profileText.append(k).append(": ").append(v).append("\n"));
        }

        // 4) Build final prompt combining:
        //    - global user profile
        //    - conversation history for this chat
        //    - new user message
        String finalPrompt =
                profileText +
                        "\nConversation so far:\n" +
                        history +
                        "\nUser: " + message;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", finalPrompt)
                                )
                        )
                )
        );

        String jsonResponse = webClient.post()
                .uri("/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String botReply = extractText(jsonResponse);

        // 5) Update chat memory for this chat only
        String newHistory = history
                + "\nUser: " + message
                + "\nBot: " + botReply + "\n";

        ChatMemoryEntity entity = chatMemoryRepo
                .findByUserIdAndChatId(userId, chatId)
                .orElse(new ChatMemoryEntity());

        entity.setUserId(userId);
        entity.setChatId(chatId);
        entity.setHistory(newHistory);
        entity.setUpdatedAt(LocalDateTime.now());

        chatMemoryRepo.save(entity);


        return botReply;
    }

    private void saveProfileToDb(String userId, String key, String value) {
        UserProfileMemoryEntity entity = userProfileRepo
                .findByUserIdAndMemoryKey(userId, key)
                .orElse(new UserProfileMemoryEntity());

        entity.setUserId(userId);
        entity.setMemoryKey(key);
        entity.setMemoryValue(value);
        entity.setUpdatedAt(LocalDateTime.now());

        userProfileRepo.save(entity);
    }


    /**
     * Parse JSON produced by extractMemoryUsingGemini and store into global profile memory.
     */
    private void storeProfileFromJson(String userId, String memoryJsonString) {
        try {
            if (memoryJsonString == null || memoryJsonString.isBlank()) {
                return;
            }

            String cleaned = memoryJsonString.trim();

            // Remove markdown ```json ... ```
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replace("```json", "")
                                 .replace("```", "")
                                 .trim();
            }
    
            // Remove text before or after JSON
            int jsonStart = cleaned.indexOf("{");
            int jsonEnd = cleaned.lastIndexOf("}");
    
            if (jsonStart == -1 || jsonEnd == -1) {
                System.out.println("No JSON found in extracted memory: " + cleaned);
                return;
            }

            cleaned = cleaned.substring(jsonStart, jsonEnd + 1).trim();

            JsonNode node = mapper.readTree(memoryJsonString);

            // Expecting either {} or {"name":"Lavya","profession":"backend developer", ...}
            if (!node.isObject() || node.size() == 0) {
                return; // nothing important
            }

            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode valueNode = entry.getValue();
                String value = valueNode.isTextual() ? valueNode.asText() : valueNode.toString();
//                profileMemory.save(userId, key, value);
                saveProfileToDb(userId, key, value);

            });

        } catch (Exception e) {
            // If parsing fails, just ignore for now
            System.err.println("Failed to parse memory JSON: " + memoryJsonString);
            e.printStackTrace();
        }
    }

    private String extractMemoryUsingGemini(String message) {

        String extractionPrompt =
        "You are a memory extraction assistant.\n" +
        "Your job is to extract personal details from the user message.\n\n" +

        "Return ONLY a JSON object. No explanation. No extra text. No markdown.\n" +
        "The JSON must contain: name, age, location, profession, skills, preferences, or other personal details.\n" +
        "If NO personal information is found, return {}.\n\n" +

        "Message: \"" + message + "\"";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", extractionPrompt)
                                )
                        )
                )
        );

        String jsonResponse = webClient.post()
                .uri("/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractText(jsonResponse); // this returns the "JSON string" Gemini produces
    }

    public GeminiService(WebClient.Builder builder, UserProfileMemory profileMemory,UserProfileMemoryRepository userProfileRepo, ChatMemoryRepository chatMemoryRepo) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.profileMemory = profileMemory;
        this.userProfileRepo = userProfileRepo;
        this.chatMemoryRepo = chatMemoryRepo;
    }

    private String extractText(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            return root.get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();
        } catch (Exception e) {
            return "Error parsing Gemini response";
        }
    }
}


