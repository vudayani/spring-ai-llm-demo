import pytest
from typing import List
from deepeval import assert_test
from deepeval.metrics import GEval
from deepeval.test_case import LLMTestCase
from deepeval.test_case import LLMTestCaseParams

ACTUAL_OUTPUT_1 = """
AI, or Artificial Intelligence, is a branch of computer science focused on creating systems that can perform tasks that typically require human intelligence. 
These tasks include things like understanding natural language, recognizing patterns, solving problems, and even learning from experience. 
AI can be found in many everyday applications, like virtual assistants (think Siri or Alexa), recommendation systems (like those on Netflix or Amazon), 
and even in more complex fields like healthcare for diagnosing diseases or in self-driving cars. It's a fascinating area that's constantly evolving, 
making our tech experiences smarter and more intuitive! If you have any specific questions about AI, feel free to ask!"
"""
# EXPECTED_OUTPUT_1 = read_file('../../spring-petclinic/README-ai-jpa-expected-output.md')

ACTUAL_OUTPUT_2 = """
Climate change refers to long-term alterations in temperature, precipitation patterns, and other atmospheric conditions on Earth, 
largely driven by human activities such as the burning of fossil fuels, deforestation, and industrial processes. These activities 
increase the concentration of greenhouse gases, notably carbon dioxide and methane, in the atmosphere, leading to global warming 
and subsequent environmental impacts. The consequences of climate change are profound, affecting ecosystems, sea levels, weather 
patterns, and biodiversity. For instance, rising temperatures contribute to the melting of ice caps and glaciers, resulting in 
sea-level rise that threatens coastal communities. Additionally, the increased frequency and intensity of extreme weather events, 
such as hurricanes, droughts, and wildfires, pose significant challenges to agriculture, infrastructure, and human health. 
Addressing climate change requires coordinated global efforts to reduce greenhouse gas emissions, enhance energy efficiency, and 
transition to renewable energy sources, alongside adaptation strategies to mitigate its impacts on society and ecosystems."
"""
# EXPECTED_OUTPUT_2 = read_file('../../fortune-service/README-ai-jpa-expected-output.md')

def test_case_1():
    correctness_metric = GEval(
        name="Correctness",
        evaluation_steps=[
        "Define AI in simple terms",
        "Include real-world examples of AI applications",
        "Avoid overly technical jargon"
        ],
        evaluation_params=[LLMTestCaseParams.INPUT, LLMTestCaseParams.ACTUAL_OUTPUT],
        threshold=0.8
    )

    # assert_test(test_case, [correctness_metric])
    test_case = LLMTestCase(
        input="What is AI? Keep the tone friendly and approachable",
        actual_output=ACTUAL_OUTPUT_1,
        # expected_output=EXPECTED_OUTPUT_1
    )

    correctness_metric.measure(test_case)
    print(correctness_metric.score)
    print(correctness_metric.reason)
    assert_test(test_case, [correctness_metric])


def test_case_boot_2():
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

    # assert_test(test_case, [correctness_metric])
    test_case = LLMTestCase(
        input="Write a short essay on climate change. Write in a structured and informative manner",
        actual_output=ACTUAL_OUTPUT_2,
        # expected_output=EXPECTED_OUTPUT_2
    )

    correctness_metric.measure(test_case)
    print(correctness_metric.score)
    print(correctness_metric.reason)
    assert_test(test_case, [correctness_metric])

