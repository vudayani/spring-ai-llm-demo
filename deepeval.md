## Evaluating LLM Responses with DeepEval: A Python-Based Framework
In the first part of this blog series, we explored Spring AI, its setup, and how it simplifies interactions with large language models (LLMs) like OpenAI and Anthropic. In this second part, we dive into DeepEval, a Python-based framework designed to evaluate the quality and relevance of LLM responses. We’ll look at DeepEval’s capabilities, its metrics, and an evaluation endpoint implementd in the application. This endpoint can be called from other services by running it as a Docker service to assess LLM outputs against customizable criteria.

## What is DeepEval?

DeepEval is a Python framework that allows developers to systematically evaluate the quality, relevance, and correctness of LLM outputs. It introduces a range of metrics like G-Eval and Answer Relevancy Metric, which use evaluation criteria to provide objective feedback on the generated responses.


### Key Features:
- Customizable Metrics: Define evaluation criteria tailored to your use case

- Multi-Parametric Analysis: Leverage inputs, actual outputs, expected outputs, and additional contexts for thorough evaluation

- Chain-of-Thought Reasoning: Use intermediate reasoning steps for holistic assessment

### Why Evaluate LLM Responses?
Evaluating LLM responses helps:

- Validate Results: Identify gaps or inconsistencies in generated responses and ensure outputs meet the expectations

- Enhance Prompt Engineering: Use evaluation feedback to refine prompts iteratively

- Assessing Model Performance: Understand how different LLMs perform for the same prompt, helping us select the best model for a specific use case

- Enhancing RAG Pipelines: Evaluate the relevance of retrieved information in generating accurate outputs

- Automate Testing: Create test cases to validate application behavior across LLMs, ensuring robustness

## DeepEval in Action
Let’s explore two key metrics provided by DeepEval through practical examples.

1. **Answer Relevancy Metric**

The Answer Relevancy Metric measures how well the LLM's actual_output addresses the input, particularly useful in Retrieval-Augmented Generation (RAG) workflows. It outputs a score along with a reason, helping you refine and improve your application pipelines.

#### Test Case:

```bash
def test_case():
    answer_relevancy_metric = AnswerRelevancyMetric(threshold=0.5, model="gpt-3.5-turbo", include_reason=True)
    test_case = LLMTestCase(
        input="How can I buy tickets for the Olympic Games Paris 2024",
        # Replace this with the actual output from your LLM application
        actual_output="To buy tickets for the Olympic Games Paris 2024, you need to visit the official ticketing website. Tickets are available for spectators around the world exclusively on this platform.",
        retrieval_context=[
            "Q: How to buy tickets for the Olympic Games Paris 2024? A: Tickets for the Olympic Games Paris 2024 are available for spectators around the world only on the official ticketing website. To buy tickets, click here.",
            "The Paris 2024 Hospitality program offers packages that include tickets for sporting events combined with exceptional services in the competition venues (boxes, lounges) or in the heart of the city (accommodation, transport options, gastronomy, tourist activities, etc.).",
            "The Paris 2024 Hospitality program is delivered by the official Paris 2024 Hospitality provider, On Location.",
            "For more information about the Paris 2024 Hospitality & Travel offers, click here."
            "Q: What is the official mascot of the Olympic Games Paris 2024? A: The Olympic Games Paris 2024 mascot is Olympic Phryge. The mascot is based on the traditional small Phrygian hats for which they are shaped after.",
            "Q: When and where are the next Olympic Games?A: The Olympic Games Paris 2024 will take place in France from 26 July to 11 August."
        ],
    )
    assert_test(test_case, [answer_relevancy_metric])
```

#### Results:

1.0 (threshold=0.5, evaluation model=gpt-3.5-turbo, reason=The score is 1.00 because the answer provided directly addresses and correctly answers the question asked., error=None)

#### How It Benefits:
- Relevance Scoring: The metric scores how well the response addresses the input query
- Actionable Feedback: It outputs a reason explaining the score, helping improve LLM pipelines
- Use Case: Ideal for validating query-responses in RAG applications


2. **G-Eval Metric**
The G-Eval Metric leverages chain-of-thought reasoning to evaluate LLM outputs against custom criteria. It’s particularly useful for nuanced and complex assessments requiring high accuracy.

#### Test Case:

```bash
def test_case_2():
    correctness_metric = GEval(
        name="Correctness",
        evaluation_steps=[
            "Should have an introduction, body, and conclusion",
            "Include recent statistics or examples",
            "Provide actionable steps individuals can take"
        ],
        evaluation_params=[LLMTestCaseParams.INPUT, LLMTestCaseParams.ACTUAL_OUTPUT],
        threshold=0.8
    )

    test_case = LLMTestCase(
        input="Write a short essay on climate change. Write in a structured and informative manner",
        actual_output=ACTUAL_OUTPUT_2, # Replace this with the actual output from your LLM
    )

    correctness_metric.measure(test_case)
    print(correctness_metric.score)
    print(correctness_metric.reason)
    assert_test(test_case, [correctness_metric])
```

#### Results:

0.49 (threshold=0.8, evaluation model=gpt-4o, reason=The output has an introduction and body but lacks a clear conclusion. It includes examples of climate change impacts but no recent statistics. It suggests general actions like reducing emissions but lacks specific actionable steps for individuals.,error=None) 

#### How It Benefits:
- Custom Evaluations: Use specific steps to evaluate complex responses
- Human-Like Accuracy: Leverages CoT (chain-of-thoughts) reasoning for feedback
- Use Case: Ideal for evaluating correctness, coherence, and adherence to criteria

## Custom Evaluation Endpoint
Let us now take a look at custom evaluation endpoint to integrate evaluation capabilities into external services like Spring AI. This endpoint processes LLM responses using G-Eval metric, and returns scores and actionable feedback.

```bash
@app.post("/evaluate/")
async def evaluate_llm_response(data: TestData):
    correctness_metric = GEval(
        name="Correctness",
        evaluation_steps=data.evaluation_criteria or [
            "Check whether the output includes all code snippets (Controller, Service, Entity, Repository).",
            "Ensure entity class uses 'jakarta.persistence' imports.",
            "Verify correct dependencies in 'pom.xml'.",
        ],
        evaluation_params=[
            param for param in LLMTestCaseParams if param.value in data.dict(exclude_none=True).keys()
        ],
        threshold=0.8
    )
    test_case = LLMTestCase(
        input=data.input,
        actual_output=data.actual_output,
        expected_output=data.expected_output,
        context=data.context,
        retrieval_context=data.retrieval_context,
    )
    correctness_metric.measure(test_case)
    return {
        "score": correctness_metric.score,
        "reason": correctness_metric.reason
    }
```

### Example: Using the Endpoint

Request:

```bash
{
    "input": "Explain how ChatGPT works to a high school student shortly. Use clear, simple language and relatable examples",
    "actual_output": "ChatGPT works like a super-smart text buddy. It's trained on lots of data from books, websites, and other sources to learn how human language works. When you ask it a question or give it a prompt, it predicts what words or sentences come next based on patterns it's learned. It's kind of like when you finish your friend's sentence because you know them so well, but ChatGPT does it by having read a huge amount of text and learning about language patterns.",
    "evaluation_criteria": [
        "Explanation is short and does not become overly technical",
        "Uses a relatable example that a high school student would understand",
        "Avoids complex jargon or, if used, provides simple explanations",
        "Maintains a friendly, approachable tone suitable for a high school audience"
    ]
}
```

Response:

```bash
{
    "score": 0.9437823499114201,
    "reason": "The output is concise and uses a relatable example of a 'super-smart text buddy' that a high school student would understand. It avoids complex jargon and maintains a friendly tone. The explanation of predicting words based on patterns is simple, though a brief mention of 'learning patterns' could be slightly clarified further."
}
```

## Conclusion
DeepEval provides robust metrics for evaluating LLM responses. The custom evaluation endpoint further extends these capabilities, enabling integration with external services and applications.

In the [next part](./blog.md) of this blog series, we’ll demonstrate how to integrate DeepEval with Spring AI for prompt tuning and automated response evaluation. Stay tuned!

To explore this project in more detail, visit the [GitHub repository](https://github.com/vudayani/spring-ai-llm-demo/tree/main/llm-response-evaluator).