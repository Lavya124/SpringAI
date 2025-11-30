# Spring Boot Gemini Chatbot

This is a simple AI chatbot built using:
- Spring Boot 3
- Java 17
- Gemini 2.5 Flash (Google AI Studio)
- WebClient HTTP integration
- JSON parsing using Jackson

## How it works

The project exposes one endpoint:

GET /chat?message=Hello

This calls `GeminiService`, which:
- Sends the request to Google Gemini API
- Receives the JSON AI response
- Extracts only the AI text
- Returns the clean chatbot reply

## How to Run

1. Add your Gemini API key in `application.properties`:



