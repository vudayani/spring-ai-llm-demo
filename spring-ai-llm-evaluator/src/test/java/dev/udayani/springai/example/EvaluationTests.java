package dev.udayani.springai.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import dev.udayani.springai.example.dto.PromptRequest;
import dev.udayani.springai.example.service.LlmEvaluationService;

@SpringBootTest
public class EvaluationTests {

	@Autowired
	private LlmEvaluationService llmEvaluationService;

	@Autowired
	private OpenAiChatModel openAiChatModel;

	@Autowired
	private AnthropicChatModel anthropicChatModel;

	@Test
    public void evalOpenAiLlmModel() throws IOException {
    	
        PromptRequest promptReq = new PromptRequest("add jpa functionality", "Your task is to create Java source code for a Spring Boot application");

        String modelResponse = llmEvaluationService.getLlmModelResponse(promptReq, "openai").getResult().getOutput()
				.getContent();
        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(openAiChatModel));
        Assert.notNull(modelResponse, "LLM model response should not be null");
        
        EvaluationRequest evaluationRequest = new EvaluationRequest(
        		promptReq.userPrompt(),
        		modelResponse);


        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(),
            "Response is not relevant to the asked question.\n" +
                "Question: " + promptReq + "\n" +
                "Response: " + modelResponse);
    }

	@Test
	public void evalAnthropicLlmModel() throws IOException {

		PromptRequest promptReq = new PromptRequest("add jpa functionality",
				"Your task is to create Java source code for a Spring Boot application");

		String modelResponse = llmEvaluationService.getLlmModelResponse(promptReq, "anthropic").getResult().getOutput()
				.getContent();
		var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(anthropicChatModel));
		Assert.notNull(modelResponse, "LLM model response should not be null");

		EvaluationRequest evaluationRequest = new EvaluationRequest(promptReq.userPrompt(), modelResponse);

		EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

		assertTrue(evaluationResponse.isPass(),
	            "Response is not relevant to the asked question.\n" +
	                "Question: " + promptReq + "\n" +
	                "Response: " + modelResponse);
	}

}
