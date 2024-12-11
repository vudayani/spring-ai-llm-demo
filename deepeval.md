# Evaluating LLM Responses with Spring AI Evaluator and DeepEval

In the first part of this blog series, we explored Spring AI, its setup, and how it simplifies interactions with large language models (LLMs) like OpenAI and Anthropic. In this second part, we shift focus to Spring AI's Evaluator API, which provides a simple interface for evaluating llm responses directly within a Spring Boot application. Then we dive into DeepEval, a Python-based framework designed to evaluate the quality and relevance of LLM responses. 
We’ll look at DeepEval’s capabilities, its metrics, and an evaluation endpoint implemented in the application. This endpoint can be called from other applications by running it as a Docker service to assess LLM outputs against customizable criteria.

## Why Evaluate LLM Responses?
Evaluating LLM responses helps:

- Validate Results: Identify gaps or inconsistencies in generated responses and ensure outputs meet the expectations

- Enhance Prompt Engineering: Use evaluation feedback to refine prompts iteratively

- Assessing Model Performance: Understand how different LLMs perform for the same prompt, helping us select the best model for a specific use case

- Enhancing RAG Pipelines: Evaluate the relevance of retrieved information in generating accurate outputs

- Automate Testing: Create test cases to validate application behavior across LLMs, ensuring robustness

## Spring AI Evaluator Interface: Built-in Response Evaluation

The Spring AI project provides an Evaluator interface that offers basic strategies to evaluate model responses within the Spring Boot application itself. Although these strategies are currently limited to RelevancyEvaluator and FactCheckingEvaluator, they provide a good starting point for evaluating LLM responses. The project continues to evolve, so it's worth giving it a try.

In this project, I have used the RelevancyEvaluator. This evaluator uses the input (userText) and the AI model’s output (chatResponse) to check if the LLM response is relevant and not hallucinated.

Here is an example of a JUnit test using the RelevancyEvaluator:

```java
@Test
public void evalOpenAiLlmModel() throws IOException {
    PromptRequest promptReq = new PromptRequest("add jpa functionality", "Your task is to create Java source code for a Spring Boot application");

    String modelResponse = llmEvaluationService.getLlmModelResponse(promptReq, "openai").getResult().getOutput().getContent();
    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(openAiChatModel));
    Assert.notNull(modelResponse, "LLM model response should not be null");

    EvaluationRequest evaluationRequest = new EvaluationRequest(
        promptReq.userPrompt(),
        modelResponse
    );

    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    assertTrue(evaluationResponse.isPass(),
        "Response is not relevant to the asked question.\n" +
        "Question: " + promptReq + "\n" +
        "Response: " + modelResponse
    );
}
```

The Spring AI Evaluator interface is ideal for quick evaluations during testing and development. It provides:

- Simplicity: Evaluate responses directly within your application
- Seamless Integration: Works natively with Spring AI’s ChatClient
- Immediate Feedback: Easily validate responses during the development process

While the Spring AI Evaluator interface is ideal for quick evaluations, complex use cases often require a more nuanced approach with customizable evaluation criteria. This is where DeepEval comes into play. In the next section, we’ll explore how DeepEval provides advanced metrics and broader evaluation capabilities.

## Introducing DeepEval: Advanced Evaluation for LLMs

DeepEval is a Python framework that allows developers to systematically evaluate the quality, relevance, and correctness of LLM outputs. It introduces a range of metrics like G-Eval and Answer Relevancy Metric, which use evaluation criteria to provide objective feedback on the generated responses.

### Key Features:
- Customizable Metrics: Define evaluation criteria tailored to your use case

- Multi-Parametric Analysis: Leverage inputs, actual outputs, expected outputs, and additional contexts for thorough evaluation

- Chain-of-Thought Reasoning: Use intermediate reasoning steps for holistic assessment

### DeepEval in Action
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

To harness DeepEval’s power in the Spring AI application, I created an HTTP REST endpoint wrapping DeepEval functionality. By running it as a Docker container, the endpoint can:
- Be easily called from Spring AI applications for evaluation
- Act as a microservice to validate LLM responses with advanced metrics
- Serve as a reusable and scalable evaluation tool

This endpoint processes LLM responses using G-Eval metric, and returns scores and actionable feedback.

Here’s the implementation of the `/evaluation` endpoint:
```bash
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from deepeval.metrics import GEval
from deepeval.test_case import LLMTestCase, LLMTestCaseParams
from deepeval import assert_test
from typing import Optional

app = FastAPI()

class TestData(BaseModel):
    input: str
    actual_output: str
    expected_output: Optional[str] = None 
    context: Optional[str] = None
    retrieval_context: Optional[list[str]] = None
    evaluation_criteria: Optional[list[str]] = None

@app.post("/evaluate/")
async def evaluate_llm_response(data: TestData):
    try:
        user_fields = data.dict(exclude_none=True).keys()
        evaluation_params = [
            param
            for param in LLMTestCaseParams
            if param.value in user_fields
        ]


        correctness_metric = GEval(
            name="Correctness",
            evaluation_steps=data.evaluation_criteria or [
                "Check whether 'actual output' has all the files or code snippets (Controller, service, entity, repository) from 'expected output'",
                "The entity class import statements should use 'jakarta.persistence' instead of 'javax.persistence'",
                "Check if the correct dependencies are added in the 'pom.xml' file",
            ],
            evaluation_params=evaluation_params,
            threshold=0.8
        )

        test_case = LLMTestCase(
            input=data.input,
            actual_output=data.actual_output,
            expected_output=data.expected_output,
            context=data.context,
            retrieval_context=data.retrieval_context
        )

        correctness_metric.measure(test_case)
        # assert_test(test_case, [correctness_metric])

        result = {
            "score": correctness_metric.score,
            "reason": correctness_metric.reason
        }

        print("Evaluation Result:", result)
        return result
        
    except Exception as e:
        print("Error:", str(e))
        raise HTTPException(status_code=500, detail=str(e))
```

- The `TestData` class defines the structure of the input data
- The endpoint accepts a POST request with TestData as request body
- If `evaluation_criteria` is not provided, it uses default criteria
- An `LLMTestCase` is created with the input data, and the correctness metric is measured against it
- It processes the input data and evaluates the LLM response using the G-Eval metric
- The endpoint returns a JSON object containing the evaluation score and reason. If an error occurs, it raises an HTTP 500 exception with the error details

The REST API allows external services, like a Spring AI application, to send LLM responses for evaluation. 

### Dockerizing the DeepEval Service
The service is containerized using Docker for easy deployment and integration into Spring AI applications.

Here is the Dockerfile:
```bash
FROM python:3.10-slim

WORKDIR /llm-response-evaluator

COPY . /llm-response-evaluator

RUN pip install fastapi uvicorn nest_asyncio deepeval

WORKDIR /llm-response-evaluator

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]
```

Build and run the Docker container:
```bash
docker build -t evaluation-service .
docker run -p 8000:8000 -e <OPENAI_API_KEY> evaluation-service
```

### Example: Using the Endpoint

This is a sample request:

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
In this blog, we explored the Spring AI Evaluator API as a built-in option for quick and seamless response evaluation within a Spring Boot application during development and testing.
For advanced use cases requiring customizable metrics and in-depth analysis, DeepEval serves as a powerful Python-based framework. Dockerizing the custom evaluation endpoint further extends these capabilities, enabling integration with external services and applications.

In the [next part](./blog.md) of this blog series, we’ll demonstrate how to integrate DeepEval with Spring AI for prompt tuning and automated response evaluation. Stay tuned!

To explore this project in more detail, visit the [GitHub repository](https://github.com/vudayani/spring-ai-llm-demo/tree/main/llm-response-evaluator).