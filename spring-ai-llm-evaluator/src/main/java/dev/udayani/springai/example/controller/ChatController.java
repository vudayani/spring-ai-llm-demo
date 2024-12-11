package dev.udayani.springai.example.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.udayani.springai.example.dto.PromptRequest;
import dev.udayani.springai.example.dto.PromptTuningRequest;
import dev.udayani.springai.example.service.LlmEvaluationService;

@RestController
@RequestMapping("/api")
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(LlmEvaluationService.class);

	private LlmEvaluationService llmEvaluationService;
	
	@Value("classpath:/prompts/spring-prompt.st")
	private Resource sbPromptTemplate;

	private static final List<String> SUPPORTED_MODELS = List.of("openai", "anthropic");

	public ChatController(LlmEvaluationService llmEvaluationService) {
		this.llmEvaluationService = llmEvaluationService;
	}

	/**
	 * Endpoint to generate LLM response based on user input and model.
	 *
	 * @param model         LLM model to use (e.g., "openai", "anthropic").
	 * @param promptRequest Optional: User and system prompts
	 * @return Generated response or an error message.
	 * @throws IOException
	 */

	@PostMapping("/askLlm")
	public ResponseEntity<String> fetchLlmResponse(@RequestParam(value = "model", defaultValue = "openai") String model,
			@RequestBody(required = false) PromptRequest promptRequest) throws IOException {
		if (!SUPPORTED_MODELS.contains(model.toLowerCase())) {
			return ResponseEntity.badRequest()
					.body("Invalid modelType. Supported models are: " + String.join(", ", SUPPORTED_MODELS));
		}

		if (promptRequest == null) {
			promptRequest = new PromptRequest("add jpa functionality",
					sbPromptTemplate.getContentAsString(Charset.defaultCharset()));
		}

		try {
			String response = llmEvaluationService.getLlmModelResponse(promptRequest, model).getResult().getOutput()
					.getContent();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error generating response for model {}: {}", model, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error generating response from model: " + e.getMessage());
		}
	}

	/**
	 * Endpoint to tune the prompts for a given model.
	 *
	 * @param model               The model to be used for prompt tuning. Defaults to "openai" if not provided.
	 * @param promptTuningRequest The request body containing the details for prompt tuning. This is optional.
	 * @return   The response entity containing the result of the prompt tuning operation.
	 * @throws IOException        If an input or output exception occurs.
	 */
	@PostMapping("/promptTuning")
	public ResponseEntity<?> promptTuning(@RequestParam(value = "model", defaultValue = "openai") String model,
			@RequestBody(required = false) PromptTuningRequest promptTuningRequest) throws IOException {
		
		if (!SUPPORTED_MODELS.contains(model.toLowerCase())) {
			return ResponseEntity.badRequest()
	                .body("Invalid modelType. Supported models are: " + String.join(", ", SUPPORTED_MODELS));
		}
		
		if(promptTuningRequest == null) {
			promptTuningRequest = new PromptTuningRequest("add jpa functionality",
					sbPromptTemplate.getContentAsString(Charset.defaultCharset()), null);
		}

		return ResponseEntity.ok(llmEvaluationService.evaluateLLMResponse(promptTuningRequest, model));

	}
}