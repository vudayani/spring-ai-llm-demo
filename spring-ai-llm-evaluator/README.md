# Spring AI LLM Evaluator

## Overview

The Spring AI LLM Evaluator is an application that integrates with large language models (LLMs) to:

1. Generate responses using pre-configured or custom prompts
2. Evaluate and refine prompts to improve response quality

This project leverages Spring AI for LLM integration and a Python-based evaluation service to score and analyze LLM-generated responses. It is designed to assist in prompt engineering, code generation, and response evaluation, making it easier to use LLMs effectively.

## Core Components
- Spring Boot Application:
	Hosts the endpoints and integrates with Spring AI for LLM access.
- Python Evaluation Service:
	- Evaluates responses and returns a score indicating their quality
    - Runs as a Docker service, accessed via REST endpoint

    For more information, check https://github.com/vudayani/spring-ai-llm-demo/tree/main/llm-response-evaluator

## Key Features

1. Code Generation Endpoint (/generateResponse)
- Purpose: Generate responses using user-defined or default prompts
- Functionality:
   - Supports custom prompts
   - Provides sample JPA code for applications by default if no custom prompts are provided

	Models Supported: OpenAI, Anthropic

2. Prompt Tuning Endpoint (/promptTuning)
- Purpose: Fine-tune prompts by evaluating LLM responses
- Functionality:
	- Evaluates response quality using a Python-based evaluation service
	- Returns feedback or guidelines when responses score below the threshold
	- Leverages the LLM itself to suggest improved prompts
     
## Getting Started
1. Prerequisites
	- Java 17+
	- Docker (for Python evaluation service)
	- Maven or Gradle (for Spring Boot application)
    - API Keys: The project supports both OpenAI and Anthropic API keys for LLM integration. However, the Python evaluation service requires an OpenAI API key to work

2. Setup
	1. Clone the repository:
	```bash
	git clone https://github.com/vudayani/spring-ai-llm-demo.git
	cd spring-ai-llm-evaluator
	```
	2. Build and start the Spring Boot application:
	```bash
	mvn clean install
	java -jar target/spring-ai-prompt-evaluator.jar
	```
	3. Set up the Python evaluation service:
	```bash
	cd llm-response-evaluator
	```
	5. Build and Run the Python evaluation service (Docker):
	```bash
	docker build -t evaluation-service .
	docker run -p 8000:8000 -e <OPENAI_API_KEY> evaluation-service
	```