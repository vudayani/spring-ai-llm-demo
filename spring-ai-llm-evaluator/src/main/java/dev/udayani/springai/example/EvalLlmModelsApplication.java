package dev.udayani.springai.example;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import dev.udayani.springai.example.service.ConfigProperties;

@EnableConfigurationProperties(value = ConfigProperties.class)
@SpringBootApplication
public class EvalLlmModelsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvalLlmModelsApplication.class, args);
	}
	
	@Bean
	public ChatClient openAIChatClient(OpenAiChatModel chatModel) {
		return ChatClient.create(chatModel);
	}

	@Bean
	public ChatClient anthropicChatClient(AnthropicChatModel chatModel) {
		return ChatClient.create(chatModel);
	}
	
	@Bean
	public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
		return ChatClient.create(chatModel);
	}

}
