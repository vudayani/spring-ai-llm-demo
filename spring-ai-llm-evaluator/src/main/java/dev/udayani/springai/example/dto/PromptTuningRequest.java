package dev.udayani.springai.example.dto;

import java.util.List;

public record PromptTuningRequest(String userPrompt, String systemPrompt, List<String> evaluationCriteria) {
}
