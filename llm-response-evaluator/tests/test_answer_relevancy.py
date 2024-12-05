import pytest
from deepeval import assert_test
from deepeval.metrics import AnswerRelevancyMetric
from deepeval.test_case import LLMTestCase


def test_case():
    answer_relevancy_metric = AnswerRelevancyMetric(threshold=0.5, model="gpt-3.5-turbo", include_reason=True)
    test_case = LLMTestCase(
        input="What sports are being included in the 2024 summer olympics",
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
