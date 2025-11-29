package com.example.demo.service;

import com.example.demo.memory.UserProfileMemory;
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
//        // 1️⃣ Get memory map for this user
//        Map<String, String> userChats =
//                memoryStore.computeIfAbsent(userId, k -> new HashMap<>());
//
//        // 2️⃣ Get memory for this chatId
//        String memory = userChats.getOrDefault(chatId, "");
//
//        // 3️⃣ Build combined prompt
//        String combinedPrompt = memory + "\nUser: " + message;
//
//        // 4️⃣ Make Gemini request
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
//        // 5️⃣ Update memory
//        String newMemory = combinedPrompt + "\nBot: " + botReply + "\n";
//        userChats.put(chatId, newMemory);
//
//        return botReply;
//    }

    public String askGemini(String userId, String chatId, String message) {

        // 1) Load chat memory for this user + chat
        Map<String, String> userChats =
                chatMemoryStore.computeIfAbsent(userId, u -> new HashMap<>());
        String history = userChats.getOrDefault(chatId, "");

        // 2) Let Gemini decide if this message contains important personal info
        String memoryJsonString = extractMemoryUsingGemini(message);
        storeProfileFromJson(userId, memoryJsonString);

        // 3) Build user profile text from global memory
        Map<String, String> profile = profileMemory.getProfile(userId);
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

        userChats.put(chatId, newHistory);

        return botReply;
    }


    /**
     * Parse JSON produced by extractMemoryUsingGemini and store into global profile memory.
     */
    private void storeProfileFromJson(String userId, String memoryJsonString) {
        try {
            if (memoryJsonString == null || memoryJsonString.isBlank()) {
                return;
            }

            JsonNode node = mapper.readTree(memoryJsonString);

            // Expecting either {} or {"name":"Lavya","profession":"backend developer", ...}
            if (!node.isObject() || node.size() == 0) {
                return; // nothing important
            }

            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode valueNode = entry.getValue();
                String value = valueNode.isTextual() ? valueNode.asText() : valueNode.toString();
                profileMemory.save(userId, key, value);
            });

        } catch (Exception e) {
            // If parsing fails, just ignore for now
            System.err.println("Failed to parse memory JSON: " + memoryJsonString);
            e.printStackTrace();
        }
    }

    private String extractMemoryUsingGemini(String message) {

        String extractionPrompt =
                "From this user message, extract ONLY important personal information " +
                        "that would help personalize future responses.\n" +
                        "Return your answer STRICTLY as JSON.\n" +
                        "If nothing important, return {}.\n\n" +
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

    public GeminiService(WebClient.Builder builder, UserProfileMemory profileMemory) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.profileMemory = profileMemory;
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
