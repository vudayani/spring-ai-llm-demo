package dev.udayani.springai.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import dev.udayani.springai.example.dto.PromptTuningRequest;
import dev.udayani.springai.example.dto.PromptTuningResult;
import dev.udayani.springai.example.service.LlmEvaluationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvaluationIntegrationTest {
	
	@Autowired
    private LlmEvaluationService llmEvaluationService;
	
	@ParameterizedTest
    @ValueSource(strings = {"openai", "anthropic"})
    public void evalLlmModels(String model) throws IOException {
    	
		List<String> evaluationCriteria = Arrays.asList(
	            "Check whether 'actual output' has all the files or code snippets (Controller, service, entity, repository) from 'expected output'",
	            "The entity class import statements should use 'jakarta.persistence' instead of 'javax.persistence'",
	            "Check if the correct dependencies are added in the 'pom.xml' file"
	        );
		
        PromptTuningRequest promptTuningReq = new PromptTuningRequest("add jpa functionality",
				"Your task is to create Java source code for a Spring Boot application", evaluationCriteria);

        PromptTuningResult evalResponse = llmEvaluationService.evaluateLLMResponse(promptTuningReq, model);
        Assert.notNull(evalResponse, "Evaluation response should not be null");

        double threshold = 0.9;
        assertThat(Double.parseDouble(evalResponse.evalResponse().score())).isGreaterThan(threshold);
    }
}

