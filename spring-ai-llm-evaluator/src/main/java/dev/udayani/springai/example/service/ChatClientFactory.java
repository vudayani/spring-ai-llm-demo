package dev.udayani.springai.example.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatClientFactory {

    private final ChatClient openAIChatClient;
    private final ChatClient anthropicChatClient;
    private final ChatClient ollamaChatClient;

    public ChatClientFactory(
            @Qualifier("openAIChatClient") ChatClient openAIChatClient,
            @Qualifier("anthropicChatClient") ChatClient anthropicChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAIChatClient = openAIChatClient;
        this.anthropicChatClient = anthropicChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public ChatClient getChatClient(String modelType) {
        if ("openai".equalsIgnoreCase(modelType)) {
            return openAIChatClient;
        } else if ("anthropic".equalsIgnoreCase(modelType)) {
            return anthropicChatClient;
        } else if ("ollama".equalsIgnoreCase(modelType)) {
            return ollamaChatClient;
        } else {
            throw new IllegalArgumentException("Invalid model type: " + modelType);
        }
    }
}

