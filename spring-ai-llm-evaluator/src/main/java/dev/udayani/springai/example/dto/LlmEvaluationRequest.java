package dev.udayani.springai.example.dto;

import java.util.List;

public record LlmEvaluationRequest(String input, String actual_output, List<String> retrieval_context,
		List<String> evaluation_criteria) {

}
