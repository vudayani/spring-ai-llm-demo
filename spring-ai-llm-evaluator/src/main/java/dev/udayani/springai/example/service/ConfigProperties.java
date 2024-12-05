package dev.udayani.springai.example.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "evaluation")
public record ConfigProperties(String apiUrl) {

}
