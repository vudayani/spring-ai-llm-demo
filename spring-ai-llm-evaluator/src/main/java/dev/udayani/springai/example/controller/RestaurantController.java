package dev.udayani.springai.example.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.udayani.springai.example.dto.PromptRequest;
import dev.udayani.springai.example.dto.Restaurant;
import dev.udayani.springai.example.service.ChatClientFactory;

@RestController
@RequestMapping("/api")
public class RestaurantController {
	
	private final ChatClientFactory chatClientFactory;
	
	public RestaurantController(ChatClientFactory chatClientFactory) {
		this.chatClientFactory = chatClientFactory;
	}
	
	@PostMapping("/fetchRestaurants")
	public List<Restaurant> fetchRestaurants(@RequestParam(value = "model", defaultValue = "openai") String model,
			@RequestBody(required = false) PromptRequest promptRequest) throws IOException {
		ChatClient chatClient = chatClientFactory.getChatClient(model);
		return chatClient.prompt()
				.system(s -> s.text(promptRequest.systemPrompt()))
	            .user(u -> u.text(promptRequest.userPrompt()))
	            .call()
	            .entity(new ParameterizedTypeReference<List<Restaurant>>() {});
	}

}
