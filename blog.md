# Spring AI with DeepEval: Prompt Tuning and Automated Evaluation
In the previous parts of this blog series, we explored Spring AI, a Java framework that simplifies LLM integration, and DeepEval, a Python-based evaluation framework for assessing LLM responses. In this final part, we demonstrate how to integrate DeepEval with Spring AI to enable prompt tuning and automated response evaluation using a sample application.

## Why Prompt Tuning
Prompt tuning transforms how developers interact with LLMs, creating a feedback loop that significantly improves response quality over time.

- Enhance Output Quality: Ensure responses are accurate, relevant, and aligned with expectations
- Boost Model Efficiency: Minimize unnecessary retries or manual intervention by crafting well-defined prompts
- Compare Model Performance: Use evaluation metrics to determine how different LLMs respond to the same prompt, enabling data-driven decisions

## Architecture Overview
The integration involves:

1. Spring AI Application: Acts as the main interface for interacting with LLMs and provides endpoints for generating responses.
2. DeepEval Evaluation Service: A Python-based microservice that evaluates the quality of LLM responses.
3. Integration Workflow: Spring AI sends prompts and responses to the DeepEval service for evaluation, receives feedback, and optionally suggests prompt refinements.

## Prompt Tuning Endpoint

The /promptTuning endpoint combines the capabilities of Spring AI and DeepEval to evaluate LLM responses and refine prompts iteratively.
Provides feedback and suggestions for improving prompts when response quality is below a threshold by leveraging the LLM itself to generate suggestions for improvement.

## Prompt Tuning Example
By experimenting with custom inputs, we can explore how prompts influence LLM responses and use evaluation feedback to iteratively improve them.

#### Initial Request
Below is a sample request (userPrompt and systemPrompt) to create a 2-day itinerary for Switzerland. The evaluationCriteria is list of steps provided to verify if the llm response is meets the criteria.

```bash
{
   "userPrompt": "Give me a 2 day itinerary to Switzerland",
   "systemPrompt": "You are a travel assistant. Answer politely",
   "evaluationCriteria": [
       "Should include places to visit in sequence and timings of operations",
       "provide some suggestions for stay near by",
       "recommend some good food places near by"
   ]
}
```

This initial request is clear but lacks detailed expectations. Now, let’s call the `/promptTuning` endpoint to evaluate how well the prompt performs.

#### Initial Response and Evaluation

Here’s what the endpoint returns:

```bash
{
"llmResponse":Switzerland is a beautiful country with a lot to offer, even if you only have two days. Here's a suggested itinerary that lets you experience some of the highlights:


### Day 1: Zurich and Lucerne


**Morning: Zurich** 
- **Old Town (Altstadt):** Start your day exploring Zurich’s charming Old Town. Wander through the narrow, winding streets and admire the medieval architecture.
- **Bahnhofstrasse:** Take a stroll down one of the world's most exclusive shopping streets. Even if shopping isn't on your agenda, it's worth a visit.
- **Lake Zurich:** Head to the lake for a leisurely walk or a short boat cruise to enjoy the stunning views of the city and the Alps.


**Afternoon: Lucerne** 
- **Travel to Lucerne:** It's about a 45-minute train ride from Zurich to Lucerne.
- **Chapel Bridge (Kapellbrücke):** Visit this iconic wooden bridge, which dates back to the 14th century.
- **Old Town:** Explore Lucerne's picturesque Old Town with its colorful buildings and cobblestone streets.
- **Lion Monument:** Don't miss the famous Lion Monument, a poignant sculpture commemorating Swiss Guards who died during the French Revolution.


**Evening:** 
- **Dinner in Lucerne:** Enjoy a traditional Swiss meal at one of the local restaurants. Try some Swiss specialties like fondue or raclette.


### Day 2: Interlaken and Jungfraujoch


**Morning: Interlaken** 
- **Travel to Interlaken:** Catch an early train to Interlaken, which takes about 2 hours from Lucerne.
- **Explore Interlaken:** Take a short walk around Interlaken, set beautifully between Lake Thun and Lake Brienz, with stunning views of the surrounding mountains.


**Afternoon: Jungfraujoch** 
- **Jungfraujoch – Top of Europe:** Take the scenic train journey up to Jungfraujoch, the highest railway station in Europe. Enjoy breathtaking views of the Aletsch Glacier and surrounding peaks.
- **Activities at Jungfraujoch:** Explore the Ice Palace, enjoy the panoramic views from the Sphinx Observatory, and if time permits, take a short hike on the snow.


**Evening:** 
- **Return to Interlaken:** Head back to Interlaken for a relaxing evening.
- **Dinner:** Choose a cozy restaurant to unwind and reflect on your day. Try some local Swiss chocolates as a sweet ending.


This itinerary provides a mix of urban exploration and breathtaking natural beauty, showcasing some of the best Switzerland has to offer in a short time. Safe travels and enjoy your trip!,
   "evalResponse": {
       "score": "0.647228993149907",
       "reason": "The itinerary includes places to visit in sequence with some operational timings like train travel. However, it lacks specific suggestions for accommodation and food places, only offering general dining recommendations"
   },
   "improvementSuggestion":**Improved User Prompt:**


"Can you help me plan a detailed 2-day itinerary for a trip to Switzerland? Please include a sequence of must-visit places along with their opening hours. Additionally, I would appreciate recommendations for nearby accommodations and dining options that offer authentic Swiss cuisine."


**Improved System Prompt:**


"As a knowledgeable travel assistant, provide a comprehensive 2-day itinerary for Switzerland that includes a sequential list of attractions to visit with their operating hours. Suggest convenient accommodations nearby and recommend local dining venues that serve authentic Swiss dishes. Ensure your response is polite, informative, and well-structured to assist the user effectively."
}
```

The endpoint returns the LLM response along with a score. The reasoning for the score explains that it partially meets expectations but is missing some critical details. We get an improvement suggestion for both system and user prompt which looks more refined. 

#### Applying the Suggested Improvements

Next, let's use the refined prompt to make another request. Here's the updated input:

```bash
{
   "userPrompt": "Can you help me plan a detailed 2-day itinerary for a trip to Switzerland? Please include a sequence of must-visit places along with their opening hours. Additionally, I would appreciate recommendations for nearby accommodations and dining options that offer authentic Swiss cuisine.",
   "systemPrompt": "As a knowledgeable travel assistant, provide a comprehensive 2-day itinerary for Switzerland that includes a sequential list of attractions to visit with their operating hours. Suggest convenient accommodations nearby and recommend local dining venues that serve authentic Swiss dishes. Ensure your response is polite, informative, and well-structured to assist the user effectively.",
   "evaluationCriteria": [
       "Should include places to visit in sequence and timings of operations",
       "provide some suggestions for stay near by",
       "recommend some good food places near by"
   ]
}
```

#### Evaluating the Refined Prompt

```bash
{
    "llmResponse":Certainly! Here's a well-rounded 2-day itinerary for your trip to Switzerland, focusing on the picturesque and culturally rich region of Lucerne and nearby areas. This itinerary will include a blend of natural beauty, cultural experiences, and Swiss culinary delights.

### Day 1: Exploring Lucerne

**Morning:**

1. **Chapel Bridge (Kapellbrücke) and Water Tower**
   - **Opening Hours:** Accessible all day
   - Begin your day with a stroll across this iconic wooden bridge, a symbol of Lucerne, adorned with beautiful paintings.

2. **The Old Town (Altstadt)**
   - **Opening Hours:** Shops and cafes typically open by 9: 00 AM
   - Wander through the charming streets, exploring the historic architecture and cozy boutiques.

**Lunch:**

- **Restaurant Fritschi**
  - **Location:** Sternenplatz 5,
    6004 Lucerne
  - **Cuisine:** Traditional Swiss
  - **Recommendation:** Try the classic Swiss Rösti or Cheese Fondue.

**Afternoon:**

3. **Swiss Transport Museum (Verkehrshaus)**
   - **Opening Hours:** 10: 00 AM - 6: 00 PM
   - Discover Switzerland’s fascinating transportation history with interactive exhibits.

4. **Lake Lucerne Boat Cruise**
   - **Operating Hours:** Various departures, check local schedules
   - Enjoy stunning views of surrounding mountains on a serene boat cruise.

**Dinner:**

- **Wirtshaus Galliker**
  - **Location:** Schützenstrasse 1,
    6003 Lucerne
  - **Cuisine:** Swiss
  - **Recommendation:** Sample the Luzerner Chügelipastete, a specialty from Lucerne.

**Accommodation:**
- **Hotel des Balances**
  - **Location:** Weinmarkt,
    6004 Lucerne
  - **Features:** Centrally located with a beautiful view of the river and Old Town.

### Day 2: Day Trip to Mount Pilatus

**Morning:**

1. **Golden Round Trip to Mount Pilatus**
   - **Cogwheel Railway Hours:** Typically from 8: 30 AM – 5: 00 PM (Check seasonal schedules)
   - Start by taking a boat from Lucerne to Alpnachstad, then ride the world’s steepest cogwheel railway up to Pilatus Kulm.

**Lunch:**

- **Pilatus Kulm Restaurant**
  - **Location:** Atop Mount Pilatus
  - **Cuisine:** Swiss and International
  - **Recommendation:** Enjoy a meal with breathtaking panoramic views.

**Afternoon:**

2. **Descend via Aerial Cableway and Gondola**
   - **Operating Hours:** Generally 8: 00 AM - 5: 00 PM (Check seasonal schedules)
   - Experience another perspective of the mountain by descending via cableway to Kriens.

3. **Explore Kriens and Return to Lucerne**
   - Take a short walk or bus back to Lucerne.

**Dinner:**

- **Zunfthausrestaurant Pfistern**
  - **Location:** Kornmarkt 4,
    6004 Lucerne
  - **Cuisine:** Swiss
  - **Recommendation:** Savor the Zürcher Geschnetzeltes, a delicious veal dish.

**Accommodation:**
- **Hotel Schweizerhof Luzern**
  - **Location:** Schweizerhofquai,
    6002 Lucerne
  - **Features:** Luxurious rooms with a view of the lake, perfect for relaxation after a day of exploration.

### Additional Tips:

- **Travel Pass:** Consider purchasing a Swiss Travel Pass for unlimited travel on public transportation, which includes boats and trains.
- **Weather:** Check the weather forecast, especially for mountain excursions, and dress in layers as conditions can change rapidly.
- **Language:** While German is the primary language in Lucerne, English is widely spoken, especially in tourist areas.

Enjoy your trip to Switzerland! If you have any further questions or need adjustments to this itinerary, feel free to ask.,
    "evalResponse": {
        "score": "0.9977022630179773",
        "reason": "The output includes a detailed 2-day itinerary with a sequence of must-visit places and their operating hours. It provides accommodation suggestions, such as Hotel des Balances and Hotel Schweizerhof Luzern, and recommends dining venues like Restaurant Fritschi and Zunfthausrestaurant Pfistern that offer Swiss cuisine."
    },
    "improvementSuggestion": null
}
```

We can see a significant improvement in the llm response. The response meets all evaluation criteria, providing a well-rounded and detailed itinerary. Evaluation scores provide an objective measure to assess response quality.

## Another Prompt Tuning Example: Generating Code

Let’s explore another example, this time focusing on generating code. We'll see how the Prompt Tuning Endpoint (/promptTuning) can help refine prompts to achieve more precise results.

#### Initial Request
Here’s the initial input for creating a REST API:
```bash
{
    "userPrompt": "Write a rest api to add numbers in spring boot",
    "systemPrompt": "Your task is to create Java source code for a Spring Boot application",
    "evaluationCriteria": [
        "It should handle edge cases like negative numbers and floats",
        "Include comments to explain input and output parameters of the API",
        "Provide exception handling",
        "Provide well-commented and efficient code"
    ]
}
```

#### Initial Response and Evaluation
Here’s the response from the LLM and the evaluation feedback:
```bash
{
"llmResponse":"To create a REST API in Spring Boot that adds two numbers, we need to follow these steps:

1. Set up a Spring Boot application.
2. Create a controller to handle the REST API request.
3. Implement the logic to add two numbers.
4. Test the API.

Let's go through each of these steps.

Step 1: Set up a Spring Boot Application

First, you need to create a new Spring Boot application. You can use Spring Initializr to bootstrap the project:

- Go to [Spring Initializr
    ](https: //start.spring.io/).
- Choose your preferred project settings (e.g., Maven or Gradle, Java version, etc.).
- Add dependencies: Spring Web.
- Generate and download the project, then unzip it and open it in your favorite IDE.

Step 2: Create a Controller

Create a new Java class named `AdditionController` inside the `src/main/java/com/example/demo` package (or any other package structure you prefer).

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdditionController {

    @GetMapping("/add")
    public int addNumbers(@RequestParam int num1, @RequestParam int num2) {
        return num1 + num2;
        }
    }
\`\`\`

Step 3: Implement the Logic

In the above code, we have implemented a simple REST controller with a single endpoint `/add`. This endpoint uses the `@GetMapping` annotation to handle GET requests. It expects two request parameters, `num1` and `num2`, and returns their sum.

Step 4: Test the API

You can test the API using a tool like Postman or simply via your browser.

- Run your Spring Boot application. You can do this by executing the `main` method in the `DemoApplication` class (generated by Spring Initializr).
- Open your browser or Postman and go to: `http: //localhost:8080/add?num1=10&num2=20`.

You should see the result of the addition, `30`, as the response.

This is a basic implementation to get you started with creating a simple REST API in Spring Boot. You can expand this example by adding more functionality or integrating it with other components as required.,
"evalResponse": {
    "score": "0.4291017667186007",
    "reason": "The implementation provides a basic setup for a REST API and includes some comments explaining the input and output. However, it lacks handling of edge cases like negative numbers and floats, exception handling is not implemented, and the code comments are minimal and not comprehensive enough for efficiency."
},
"improvementSuggestion":**Improved User Prompt:**

"Create a REST API using Spring Boot for adding two numbers. The API should handle edge cases such as negative numbers and decimal values (floats). Ensure that the implementation includes comprehensive exception handling for invalid inputs and provide detailed comments explaining the input parameters, output, and logic within the code. The solution should be efficient and well-documented."

**Improved System Prompt:**

"Your task is to develop a Java REST API using Spring Boot that performs the addition of two numbers. The API should accommodate a variety of inputs, including negative numbers and floating-point values. Make sure to implement robust exception handling to manage invalid or unexpected inputs gracefully. Include detailed comments in your code to explain the purpose and usage of input parameters, the expected output, and the logic behind any key operations. The code should be efficient and clearly structured for easy understanding and maintainability."
}
```

The initial response provides a basic implementation, but it falls short of meeting the criteria. The evaluation feedback highlights missing elements like edge case handling, exception management, and detailed comments. The improvement suggestions refine both the userPrompt and systemPrompt, providing clear and specific instructions for a better response.

#### Applying the Suggested Improvements
Using the refined prompts, we make another request:

```bash
{
    "userPrompt": "Create a REST API using Spring Boot for adding two numbers. The API should handle edge cases such as negative numbers and decimal values (floats). Ensure that the implementation includes comprehensive exception handling for invalid inputs and provide detailed comments explaining the input parameters, output, and logic within the code. The solution should be efficient and well-documented.",
    "systemPrompt": "Your task is to develop a Java REST API using Spring Boot that performs the addition of two numbers. The API should accommodate a variety of inputs, including negative numbers and floating-point values. Make sure to implement robust exception handling to manage invalid or unexpected inputs gracefully. Include detailed comments in your code to explain the purpose and usage of input parameters, the expected output, and the logic behind any key operations. The code should be efficient and clearly structured for easy understanding and maintainability.",
    "evaluationCriteria": [
        "It should handle edge cases like negative numbers and floats",
        "Include comments to explain input and output parameters of the API",
        "Provide exception handling",
        "Provide well-commented and efficient code"
    ]
}
```

#### Refined Response and Evaluation
Here’s the updated response and feedback:
```bash
{
"llmResponse":"To create a REST API using Spring Boot that adds two numbers, including handling for negative numbers and decimal values, follow the steps below. We'll define a REST controller with a single endpoint to perform the addition operation, and include exception handling to manage invalid inputs. 

### Step-by-step Guide

1. **Set up Spring Boot Project**: Use Spring Initializr to create a new Spring Boot project with the necessary dependencies. You can choose dependencies like Spring Web for RESTful APIs.

2. **Create the Controller**: Implement a controller class that handles HTTP requests.

3. **Define the Addition API**: Create an endpoint to accept two numbers as input parameters and return their sum.

4. **Exception Handling**: Add exception handling to manage invalid inputs gracefully.

Here is the code for the REST API:

```java
package com.example.additionapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller annotation to define a REST controller
@RestController
@RequestMapping("/api")
public class AdditionController {
        /**
     * Endpoint to add two numbers.
     * 
     * @param num1 the first number to add. It can be an integer or a floating-point number.
     * @param num2 the second number to add. It can be an integer or a floating-point number.
     * @return ResponseEntity containing the result of the addition or an error message if the input is invalid.
     */
    @GetMapping("/add")
    public ResponseEntity<String> addNumbers(@RequestParam String num1, @RequestParam String num2) {
        try {
                // Convert input parameters to double
            double number1 = Double.parseDouble(num1);
            double number2 = Double.parseDouble(num2);

            // Perform the addition
            double sum = number1 + number2;

            // Return the result in a ResponseEntity
            return ResponseEntity.ok("The sum is: " + sum);
            } catch (NumberFormatException e) {
                // Handle the case where input parameters are not valid numbers
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: Please enter valid numbers.");
            }
        }
    }
\`\`\`

Explanation:

- **Controller**: The `AdditionController` is annotated with `@RestController`, making it a special type of controller used in RESTful web services. The `@RequestMapping("/api")` annotation specifies that this controller will handle requests to the `/api` path.

- **Endpoint**: The `@GetMapping("/add")` annotation defines a GET endpoint at `/api/add`. The method `addNumbers` accepts two request parameters `num1` and `num2`, which are expected to be strings initially.

- **Input Parameters**: Both `num1` and `num2` are strings and are then parsed into doubles using `Double.parseDouble()`. This allows the function to handle both integer and floating-point values, including negative numbers.

- **Exception Handling**: The try-catch block captures `NumberFormatException`, which occurs if the input strings are not valid numbers. In such a case, the API returns a `400 Bad Request` status with an appropriate error message.

- **Output**: If the inputs are valid, the sum is calculated and returned as a response with `200 OK` status, formatted as a user-friendly string.

Running the Application:

1. **Build and Run**: Use Maven or Gradle to build and run the Spring Boot application.
2. **Test the API**: Use tools like Postman or a web browser to test the API endpoint, e.g., `http: //localhost:8080/api/add?num1=5&num2=3`.

This setup provides a clear, efficient, and well-documented REST API for adding two numbers, with robust handling for various input scenarios.,
"evalResponse": {
    "score": "0.9180526880684206",
    "reason": "The implementation handles edge cases with parsing of negative numbers and floats, includes exception handling for invalid inputs, and provides detailed comments explaining parameters and logic. The code is efficient and clearly structured. However, more specific comments about the output format could improve clarity."
},
"improvementSuggestion": null
}
```

The solution now includes edge case handling, exception management, and well-documented code, achieving a high evaluation score.

In this era, where prompt engineering is pivotal to harnessing the potential of LLMs - the process of refining prompts and re-evaluating ensures continuous improvement. It also helps us evaluate how different LLMs perform for the same prompt.

## Spring AI Evaluator Interface

The Spring AI project provides an Evaluator API that offers basic strategies to evaluate model responses within the Spring Boot application itself, offering an alternative to Python-based frameworks. Although these strategies are currently limited to RelevancyEvaluator and FactCheckingEvaluator, they provide a good starting point for evaluating LLM responses. The project continues to evolve, so it's worth giving it a try.

In my project, I have used the RelevancyEvaluator. This evaluator uses the input (userText) and the AI model’s output (chatResponse) to check if the LLM response is relevant and not hallucinated.

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

## Conclusion

In this blog series, we explored the capabilities of Spring AI and DeepEval, and demonstrated how to combine the two for prompt tuning and automated response evaluation.  
This project serves as an example of how Spring AI can enable rapid prototyping and learning, showcasing its potential for real-world applications.
If you’re a Java developer curious about the LLM space, I encourage you to explore Spring AI and see how it can transform your workflow.