package com.example.demo.controller;

import com.example.demo.service.GeminiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

//    @GetMapping
//    public String chat(@RequestParam String message) {
//        return geminiService.askGemini(message);
//    }


    //Per session
//    @GetMapping
//    public String chat(@RequestParam String message, HttpSession session) {
//        System.out.println("SESSION ID: " + session.getId());
//        return geminiService.askGemini(message, session);
//    }


    //per user  has many chat id's
    @GetMapping
    public String chat(@RequestParam String userId, @RequestParam String chatId, @RequestParam String message) {
        return geminiService.askGemini(userId, chatId, message);
    }


}
