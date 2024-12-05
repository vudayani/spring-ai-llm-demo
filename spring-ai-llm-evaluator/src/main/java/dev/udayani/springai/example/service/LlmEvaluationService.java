package dev.udayani.springai.example.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import dev.udayani.springai.example.dto.LlmEvaluationRequest;
import dev.udayani.springai.example.dto.LlmEvaluationResponse;
import dev.udayani.springai.example.dto.PromptRequest;
import dev.udayani.springai.example.dto.PromptTuningRequest;
import dev.udayani.springai.example.dto.PromptTuningResult;

@Service
public class LlmEvaluationService {
	
	private static final Logger logger = LoggerFactory.getLogger(LlmEvaluationService.class);

	private final ChatClientFactory chatClientFactory;

	private final RestClient restClient;
	
	private final ConfigProperties props;
	
	private String improvementPromptMessage = """
			The following prompt did not meet the evaluation criteria:
			User Prompt: %s\n
			System Prompt: %s\n\n
			Evaluation Criteria: \n%s\n\n
			Please suggest improvements to both **user prompt** and the **system prompt** to better satisfy the evaluation criteria. Ensure the refined prompts are detailed, structured, and specific enough to guide the LLM in producing high-quality responses.
			""";

	public LlmEvaluationService(ChatClientFactory chatClientFactory, ConfigProperties props) {
		this.chatClientFactory = chatClientFactory;
		this.props = props;
		this.restClient = RestClient.create(this.props.apiUrl());
	}
	
    public ChatResponse getLlmModelResponse(PromptRequest promptRequest, String model) throws IOException {
		ChatClient chatClient = chatClientFactory.getChatClient(model);
		return chatClient.prompt()
			            .system(s -> s.text(promptRequest.systemPrompt()))
			            .user(u -> u.text(promptRequest.userPrompt()))
			            .call()
			            .chatResponse();
    }
    
	public PromptTuningResult evaluateLLMResponse(PromptTuningRequest promptTuningRequest, String model) throws IOException {
		PromptRequest promptRequest = new PromptRequest(promptTuningRequest.userPrompt(), promptTuningRequest.systemPrompt());
		ChatResponse chatResp = getLlmModelResponse(promptRequest, model);
		String llmResp = chatResp.getResult().getOutput().getContent();
		LlmEvaluationResponse evaluationResult = evaluateResponse(promptTuningRequest, llmResp);
		
		if (Double.parseDouble(evaluationResult.score()) < 0.7) {
			PromptRequest improvementPromptRequest = new PromptRequest(
					String.format(improvementPromptMessage, promptTuningRequest.userPrompt(),
							promptTuningRequest.systemPrompt(),
							String.join("\n", promptTuningRequest.evaluationCriteria())),
			        "Use prompt engineering techniques to deliver improved prompts that guide the LLM to produce high-quality and relevant results that meet the evaluation criteria. Ensure the system prompt provides clear role guidance.");
			System.out.println("improved prompt req " + improvementPromptRequest.toString());
			
	        String improvementSuggestion = getLlmModelResponse(
	        		improvementPromptRequest, model)
	                .getResult().getOutput().getContent();
	        return new PromptTuningResult(llmResp, evaluationResult, improvementSuggestion);
	    }
		return new PromptTuningResult(llmResp, evaluationResult, null);
	}
	
	private LlmEvaluationResponse evaluateResponse(PromptTuningRequest promptTuningRequest, String llmResp) throws IOException {
		try {
			LlmEvaluationRequest llmEvalReq = buildEvalRequest(promptTuningRequest, llmResp);
			return restClient.post().uri("evaluate/").body(llmEvalReq).retrieve().body(LlmEvaluationResponse.class);
		} catch (Exception e) {
			logger.error("Evaluation service error: " + e.getMessage());
			throw new IOException("Evaluation failed. Please try again later.", e);
		}
	}
	
	public LlmEvaluationRequest buildEvalRequest(PromptTuningRequest promptTuningRequest, String llmResponse) {
	    return new LlmEvaluationRequest(
	        "User Prompt: "+  promptTuningRequest.userPrompt() +"\n System Prompt: " + promptTuningRequest.systemPrompt() ,
	        llmResponse,
	        null,
	        promptTuningRequest.evaluationCriteria()
	    );
	}
}
